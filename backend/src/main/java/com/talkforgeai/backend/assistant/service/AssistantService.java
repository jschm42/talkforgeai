/*
 * Copyright (c) 2023-2024 Jean Schmitz.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.talkforgeai.backend.assistant.service;

import com.talkforgeai.backend.assistant.domain.AssistantEntity;
import com.talkforgeai.backend.assistant.domain.AssistantPropertyValue;
import com.talkforgeai.backend.assistant.domain.MessageEntity;
import com.talkforgeai.backend.assistant.domain.ThreadEntity;
import com.talkforgeai.backend.assistant.dto.AssistantDto;
import com.talkforgeai.backend.assistant.dto.GenerateImageResponse;
import com.talkforgeai.backend.assistant.dto.MessageListParsedDto;
import com.talkforgeai.backend.assistant.dto.ParsedMessageDto;
import com.talkforgeai.backend.assistant.dto.ProfileImageUploadResponse;
import com.talkforgeai.backend.assistant.dto.ThreadDto;
import com.talkforgeai.backend.assistant.dto.ThreadTitleDto;
import com.talkforgeai.backend.assistant.dto.ThreadTitleGenerationRequestDto;
import com.talkforgeai.backend.assistant.dto.ThreadTitleUpdateRequestDto;
import com.talkforgeai.backend.assistant.exception.AssistentException;
import com.talkforgeai.backend.assistant.repository.AssistantRepository;
import com.talkforgeai.backend.assistant.repository.MessageRepository;
import com.talkforgeai.backend.assistant.repository.ThreadRepository;
import com.talkforgeai.backend.storage.FileStorageService;
import com.talkforgeai.backend.transformers.MessageProcessor;
import com.talkforgeai.service.openai.AssistantStreamService;
import com.talkforgeai.service.openai.dto.StreamResponse;
import com.theokanning.openai.ListSearchParameters;
import com.theokanning.openai.OpenAiResponse;
import com.theokanning.openai.assistants.Assistant;
import com.theokanning.openai.assistants.AssistantRequest;
import com.theokanning.openai.assistants.ModifyAssistantRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.ImageResult;
import com.theokanning.openai.messages.Message;
import com.theokanning.openai.messages.MessageRequest;
import com.theokanning.openai.model.Model;
import com.theokanning.openai.runs.Run;
import com.theokanning.openai.runs.RunCreateRequest;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.threads.Thread;
import com.theokanning.openai.threads.ThreadRequest;
import jakarta.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

@Service
public class AssistantService {

  public static final Logger LOGGER = LoggerFactory.getLogger(AssistantService.class);

  private final OpenAiService openAiService;

  private final AssistantStreamService assistantStreamService;
  private final AssistantRepository assistantRepository;
  private final MessageRepository messageRepository;
  private final ThreadRepository threadRepository;

  private final FileStorageService fileStorageService;

  private final MessageProcessor messageProcessor;

  private final AssistantMapper assistantMapper;

  public AssistantService(OpenAiService openAiService,
      AssistantStreamService assistantStreamService,
      AssistantRepository assistantRepository, MessageRepository messageRepository,
      ThreadRepository threadRepository, FileStorageService fileStorageService,
      MessageProcessor messageProcessor, AssistantMapper assistantMapper) {
    this.openAiService = openAiService;
    this.assistantStreamService = assistantStreamService;
    this.assistantRepository = assistantRepository;
    this.messageRepository = messageRepository;
    this.threadRepository = threadRepository;
    this.fileStorageService = fileStorageService;
    this.messageProcessor = messageProcessor;
    this.assistantMapper = assistantMapper;
  }

  public AssistantDto retrieveAssistant(String assistantId) {
    Assistant assistant = this.openAiService.retrieveAssistant(assistantId);

    if (assistant != null) {
      Optional<AssistantEntity> assistantEntity = assistantRepository.findById(assistant.getId());

      if (assistantEntity.isPresent()) {
        return assistantMapper.mapAssistantDto(assistant, assistantEntity.get());
      }
    }

    return null;
  }

  public List<AssistantDto> listAssistants(ListSearchParameters listAssistantsRequest) {

    OpenAiResponse<Assistant> assistantOpenAiResponse = this.openAiService.listAssistants(
        listAssistantsRequest);

    List<AssistantDto> assistantDtoList = new ArrayList<>();

    assistantOpenAiResponse.data.forEach(assistant -> {
      Optional<AssistantEntity> assistantEntity = assistantRepository.findById(assistant.getId());

      assistantEntity.ifPresent(entity -> {
        AssistantDto assistantDto = assistantMapper.mapAssistantDto(
            assistant,
            entity
        );

        assistantDtoList.add(assistantDto);

      });
    });

    return assistantDtoList;
  }

  @Transactional
  public void syncAssistants() {
    ListSearchParameters searchParameters = new ListSearchParameters();
    OpenAiResponse<Assistant> assistantList = this.openAiService.listAssistants(
        searchParameters);
    List<AssistantEntity> assistantEntities = assistantRepository.findAll();

    // Create
    assistantList.data.forEach(assistant -> {
      LOGGER.info("Syncing assistant: {}", assistant.getId());

      Optional<AssistantEntity> assistantEntity = assistantEntities.stream()
          .filter(entity -> entity.getId().equals(assistant.getId()))
          .findFirst();

      if (assistantEntity.isEmpty()) {
        LOGGER.info("New assistant detected. Creating entity: {}", assistant.getId());

        AssistantEntity entity = new AssistantEntity();
        entity.setId(assistant.getId());

        // Map persona.properties() to Map<String, PersonaPropertyValue>
        Arrays.stream(AssistantProperties.values()).forEach(p -> {
          AssistantPropertyValue propertyValue = new AssistantPropertyValue();
          String value = p.getDefaultValue();
          propertyValue.setPropertyValue(value);
          entity.getProperties().put(p.getKey(), propertyValue);
        });

        assistantRepository.save(entity);
      }
    });

    // Delete
    assistantEntities.forEach(entity -> {
      Optional<Assistant> assistant = assistantList.data.stream()
          .filter(a -> a.getId().equals(entity.getId()))
          .findFirst();

      if (assistant.isEmpty()) {
        LOGGER.info("Assistant not found. Deleting entity: {}", entity.getId());
        assistantRepository.delete(entity);
      }
    });
  }

  @Transactional
  public ThreadDto createThread() {
    ThreadRequest threadRequest = new ThreadRequest();
    Thread thread = this.openAiService.createThread(threadRequest);

    ThreadEntity threadEntity = new ThreadEntity();
    threadEntity.setId(thread.getId());
    threadEntity.setTitle("<no title>");
    threadEntity.setCreatedAt(new Date(thread.getCreatedAt()));
    threadRepository.save(threadEntity);

    return mapToDto(threadEntity);
  }

  public List<ThreadDto> retrieveThreads() {
    return this.threadRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
        .map(this::mapToDto)
        .toList();
  }

  public Message postMessage(String threadId, MessageRequest messageRequest) {
    return this.openAiService.createMessage(threadId, messageRequest);
  }

  public Run runConversation(String threadId, RunCreateRequest runCreateRequest) {
    return this.openAiService.createRun(threadId, runCreateRequest);
  }

  public Flux<ServerSentEvent<StreamResponse>> streamRunConversation(String threadId,
      RunCreateRequest runCreateRequest) {
    return this.assistantStreamService.stream(threadId, runCreateRequest);
  }

  public MessageListParsedDto listMessages(String threadId,
      ListSearchParameters listSearchParameters) {

    OpenAiResponse<Message> messageList = this.openAiService.listMessages(threadId,
        listSearchParameters);
    Map<String, String> parsedMessages = new HashMap<>();

    messageList.data.forEach(message -> {
      Optional<MessageEntity> messageEntity = messageRepository.findById(message.getId());
      messageEntity.ifPresent(
          entity -> parsedMessages.put(message.getId(), entity.getParsedContent()));
    });

    return new MessageListParsedDto(messageList, parsedMessages);
  }

  public Run retrieveRun(String threadId, String runId) {
    return this.openAiService.retrieveRun(threadId, runId);
  }

  public Run cancelRun(String threadId, String runId) {
    return openAiService.cancelRun(threadId, runId);
  }

  private ThreadDto mapToDto(ThreadEntity threadEntity) {
    return new ThreadDto(threadEntity.getId(), threadEntity.getTitle(),
        threadEntity.getCreatedAt());
  }

  @Transactional
  public ParsedMessageDto postProcessMessage(String threadId, String messageId) {
    LOGGER.info("Post processing message: {}", messageId);
    Message message = this.openAiService.retrieveMessage(threadId, messageId);
    Optional<MessageEntity> messageEntity = messageRepository.findById(messageId);

    MessageEntity newMessageEntity = null;
    if (messageEntity.isPresent()) {
      newMessageEntity = messageEntity.get();
    } else {
      newMessageEntity = new MessageEntity();
      newMessageEntity.setId(message.getId());
      newMessageEntity.setParsedContent("");
    }

    String transformed = messageProcessor.transform(
        message.getContent().get(0).getText().getValue(),
        threadId, messageId);
    newMessageEntity.setParsedContent(transformed);

    messageRepository.save(newMessageEntity);

    ParsedMessageDto parsedMessageDto = new ParsedMessageDto();
    parsedMessageDto.setMessage(message);
    parsedMessageDto.setParsedContent(transformed);

    return parsedMessageDto;
  }

  public byte[] getImage(String threadId, String filename) throws IOException {
    Path imgFilePath = fileStorageService.getThreadDirectory().resolve(threadId).resolve(filename);
    Resource resource = new FileSystemResource(imgFilePath);
    return StreamUtils.copyToByteArray(resource.getInputStream());
  }

  @Transactional
  public ThreadTitleDto generateThreadTitle(String threadId,
      ThreadTitleGenerationRequestDto request) {

    ThreadEntity threadEntity = threadRepository.findById(threadId)
        .orElseThrow(() -> new AssistentException("Thread not found"));

    String content = """
        Generate a title in less than 6 words for the following message: %s
        """.formatted(request.userMessageContent());

    ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), content);

    ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
        .builder()
        .model("gpt-3.5-turbo")
        .messages(List.of(chatMessage))
        .maxTokens(256)
        .build();

    ChatMessage responseMessage = openAiService.createChatCompletion(chatCompletionRequest)
        .getChoices().get(0).getMessage();

    String generatedTitle = responseMessage.getContent();

    String parsedTitle = generatedTitle.replaceAll("\"", "");
    threadEntity.setTitle(parsedTitle);
    threadRepository.save(threadEntity);

    return new ThreadTitleDto(generatedTitle);
  }

  public ThreadDto retrieveThread(String threadId) {
    return threadRepository.findById(threadId)
        .map(this::mapToDto)
        .orElseThrow(() -> new AssistentException("Thread not found"));
  }

  @Transactional
  public void modifyAssistant(String assistantId, AssistantDto modifiedAssistant) {
    Assistant assistant = openAiService.retrieveAssistant(assistantId);

    if (assistant == null) {
      throw new AssistentException("Assistant not found");
    }

    AssistantEntity assistantEntity = assistantRepository.findById(assistantId)
        .orElseThrow(() -> new AssistentException("Assistant entity not found"));

    assistantEntity.setImagePath(modifiedAssistant.imagePath());
    assistantEntity.setProperties(assistantMapper.mapProperties(modifiedAssistant.properties()));

    assistantRepository.save(assistantEntity);

    ModifyAssistantRequest modifyAssistantRequest = new ModifyAssistantRequest();
    modifyAssistantRequest.setName(modifiedAssistant.name());
    modifyAssistantRequest.setDescription(modifiedAssistant.description());
    modifyAssistantRequest.setModel(modifiedAssistant.model());
    modifyAssistantRequest.setInstructions(modifiedAssistant.instructions());
    modifyAssistantRequest.setTools(modifiedAssistant.tools());
    modifyAssistantRequest.setFileIds(modifiedAssistant.fileIds());
    modifyAssistantRequest.setMetadata(modifiedAssistant.metadata());

    openAiService.modifyAssistant(assistantId, modifyAssistantRequest);
  }

  public GenerateImageResponse generateImage(String prompt) throws IOException {
    CreateImageRequest request = new CreateImageRequest();
    request.setPrompt(prompt);
    request.setN(1);
    request.setSize("1024x1024");
    request.setModel("dall-e-3");
    request.setStyle("natural");

    ImageResult image = openAiService.createImage(request);

    return new GenerateImageResponse(downloadImage(image.getData().get(0).getUrl()));
  }

  private String downloadImage(String imageUrl) throws IOException {
    String fileName = UUID.randomUUID() + "_image.png";
    Path subDirectoryPath = fileStorageService.getAssistantsDirectory();
    Path localFilePath = subDirectoryPath.resolve(fileName);

    // Ensure the directory exists and is writable
    if (!Files.exists(subDirectoryPath)) {
      Files.createDirectories(subDirectoryPath);
    }
    if (!Files.isWritable(subDirectoryPath)) {
      throw new IOException("Directory is not writable: " + subDirectoryPath);
    }

    LOGGER.info("Downloading image {}...", imageUrl);

    try {
      URI uri = URI.create(imageUrl);
      try (InputStream in = uri.toURL().openStream()) {
        Files.copy(in, localFilePath, StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (Exception ex) {
      LOGGER.error("Failed to download image: {}", imageUrl);
      throw ex;
    }

    return fileName;
  }


  public ProfileImageUploadResponse uploadImage(MultipartFile file) {
    if (file.isEmpty()) {
      throw new IllegalArgumentException("File is empty");
    }

    try {
      byte[] bytes = file.getBytes();

      String fileEnding = file.getOriginalFilename()
          .substring(file.getOriginalFilename().lastIndexOf("."));
      String filename = UUID.randomUUID() + fileEnding;

      Path path = fileStorageService.getAssistantsDirectory().resolve(filename);
      Files.write(path, bytes);

      if (!isImageFile(path)) {
        Files.delete(path);
        throw new AssistentException("File is not an image.");
      }

      return new ProfileImageUploadResponse(filename);
    } catch (IOException e) {
      throw new AssistentException("Failed to upload file", e);
    }
  }

  private boolean isImageFile(Path filePath) {
    try {
      BufferedImage image = ImageIO.read(filePath.toFile());
      return image != null;
    } catch (IOException e) {
      return false;
    }
  }


  @Transactional
  public AssistantDto createAssistant(AssistantDto modifiedAssistant) {
    AssistantRequest assistantRequest = new AssistantRequest();
    assistantRequest.setName(modifiedAssistant.name());
    assistantRequest.setDescription(modifiedAssistant.description());
    assistantRequest.setModel(modifiedAssistant.model());
    assistantRequest.setInstructions(modifiedAssistant.instructions());
    assistantRequest.setTools(modifiedAssistant.tools());
    assistantRequest.setFileIds(modifiedAssistant.fileIds());
    assistantRequest.setMetadata(modifiedAssistant.metadata());

    Assistant newAssistant = openAiService.createAssistant(assistantRequest);

    AssistantEntity assistantEntity = new AssistantEntity();
    assistantEntity.setId(newAssistant.getId());
    assistantEntity.setImagePath(modifiedAssistant.imagePath());
    assistantEntity.setProperties(assistantMapper.mapProperties(modifiedAssistant.properties()));

    assistantRepository.save(assistantEntity);

    return assistantMapper.mapAssistantDto(newAssistant, assistantEntity);
  }

  public List<String> retrieveModels() {
    return openAiService.listModels().stream()
        .map(Model::getId)
        .filter(id -> id.startsWith("gpt") && !id.contains("instruct"))
        .toList();
  }

  @Transactional
  public void deleteAssistant(String assistantId) {
    openAiService.deleteAssistant(assistantId);
    assistantRepository.deleteById(assistantId);
  }

  @Transactional
  public void deleteThread(String threadId) {
    threadRepository.deleteById(threadId);
  }

  @Transactional
  public ThreadTitleDto updateThreadTitle(String threadId, ThreadTitleUpdateRequestDto request) {
    ThreadEntity threadEntity = threadRepository.findById(threadId)
        .orElseThrow(() -> new AssistentException("Thread not found"));

    threadEntity.setTitle(request.title());
    threadRepository.save(threadEntity);

    return new ThreadTitleDto(request.title());
  }
}
