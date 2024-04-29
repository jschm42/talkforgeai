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
import com.talkforgeai.backend.assistant.domain.MessageEntity;
import com.talkforgeai.backend.assistant.domain.ThreadEntity;
import com.talkforgeai.backend.assistant.dto.AssistantDto;
import com.talkforgeai.backend.assistant.dto.GenerateImageResponse;
import com.talkforgeai.backend.assistant.dto.MessageDto;
import com.talkforgeai.backend.assistant.dto.MessageListParsedDto;
import com.talkforgeai.backend.assistant.dto.ModelSystem;
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
import com.theokanning.openai.ListSearchParameters;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.ImageResult;
import com.theokanning.openai.model.Model;
import com.theokanning.openai.service.OpenAiService;
import jakarta.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

@Service
public class AssistantSpringService {

  public static final Logger LOGGER = LoggerFactory.getLogger(AssistantSpringService.class);

  private final OpenAiService openAiService;

  private final OpenAiChatClient chatClient;

  private final AssistantRepository assistantRepository;
  private final MessageRepository messageRepository;
  private final ThreadRepository threadRepository;

  private final FileStorageService fileStorageService;

  private final MessageProcessor messageProcessor;

  private final AssistantMapper assistantMapper;

  private final UniqueIdGenerator uniqueIdGenerator;

  public AssistantSpringService(OpenAiService openAiService, OpenAiChatClient chatClient,
      AssistantRepository assistantRepository, MessageRepository messageRepository,
      ThreadRepository threadRepository, FileStorageService fileStorageService,
      MessageProcessor messageProcessor, AssistantMapper assistantMapper,
      UniqueIdGenerator uniqueIdGenerator) {
    this.openAiService = openAiService;
    this.chatClient = chatClient;
    this.assistantRepository = assistantRepository;
    this.messageRepository = messageRepository;
    this.threadRepository = threadRepository;
    this.fileStorageService = fileStorageService;
    this.messageProcessor = messageProcessor;
    this.assistantMapper = assistantMapper;
    this.uniqueIdGenerator = uniqueIdGenerator;
  }

  public AssistantDto retrieveAssistant(String assistantId) {
    AssistantEntity assistant = assistantRepository.findById(assistantId)
        .orElseThrow(() -> new AssistentException("Assistant not found"));

    if (assistant != null) {
      Optional<AssistantEntity> assistantEntity = assistantRepository.findById(assistant.getId());

      if (assistantEntity.isPresent()) {
        return assistantMapper.toDto(assistantEntity.get());
      }
    }

    return null;
  }

  public List<AssistantDto> listAssistants(Integer limit, String order) {
    List<AssistantEntity> assistants = assistantRepository.findAll();

    return assistants.stream()
        .map(assistantMapper::toDto)
        .toList();
  }

  @Transactional
  public ThreadDto createThread() {
    ThreadEntity threadEntity = new ThreadEntity();
    threadEntity.setId(uniqueIdGenerator.generateThreadId());
    threadEntity.setTitle("<no title>");
    threadEntity.setCreatedAt(new Date());
    threadRepository.save(threadEntity);

    return assistantMapper.toDto(threadEntity);
  }

  public List<ThreadDto> retrieveThreads() {
    return this.threadRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
        .map(assistantMapper::toDto)
        .toList();
  }

  public Flux<ServerSentEvent<String>> streamRunConversation(String assistantId, String threadId,
      String message) {

    Mono<Object> saveUserMessageMono = Mono.fromRunnable(
            () -> saveNewMessage(assistantId, threadId, MessageType.USER,
                message, message))  // Wrap blocking call
        .subscribeOn(Schedulers.boundedElastic());

    Mono<AssistantDto> assistantEntityMono = Mono.fromCallable(
            () -> assistantRepository.findById(assistantId)
                .orElseThrow(() -> new AssistentException("Assistant not found")))
        .map(assistantMapper::toDto)
        .subscribeOn(Schedulers.boundedElastic());

    Mono<List<MessageDto>> pastMessages = Mono.fromCallable(
            () -> messageRepository.findByThreadId(threadId, Sort.by(Direction.ASC, "createdAt")))
        .map(assistantMapper::toDto)
        .subscribeOn(Schedulers.boundedElastic());

    Mono<Tuple2<AssistantDto, List<MessageDto>>> streamContextTuple = Mono.zip(
        assistantEntityMono,
        pastMessages);

    StringBuilder assistantMessageContent = new StringBuilder();

    return saveUserMessageMono
        .then(streamContextTuple)
        .flux()
        .flatMap(tuple -> {
          AssistantDto assistantDto = tuple.getT1();
          List<MessageDto> pastMessagesList = tuple.getT2();

          List<Message> promptMessageList = pastMessagesList.stream()
              .map(m -> {
                if (MessageType.USER.equals(m.role())) {
                  return (Message) new UserMessage(m.rawContent());
                } else if (MessageType.ASSISTANT.equals(m.role())) {
                  return (Message) new AssistantMessage(m.rawContent());
                }
                throw new AssistentException("Unknown message type: " + m.role());
              })
              .toList();

          List<Message> finalPromptMessageList = new ArrayList<>(promptMessageList);
          finalPromptMessageList.addFirst(new SystemMessage(assistantDto.instructions()));
          finalPromptMessageList.add(new UserMessage(message));

          OpenAiChatOptions options = getPromptOptions(assistantDto);
          LOGGER.debug("Starting stream with prompt: {}", finalPromptMessageList);
          LOGGER.debug("Prompt Options: {}", printPromptOptions(assistantDto.system(), options));
          return chatClient.stream(new Prompt(finalPromptMessageList, options));
        })
        .mapNotNull(chatResponse -> {
          String content = chatResponse.getResult().getOutput().getContent();
          LOGGER.info("ChatResponse received: {}", content);

          if (content != null) {
            assistantMessageContent.append(chatResponse.getResult().getOutput().getContent());
          }

          ServerSentEvent<String> responseSseEvent = createResponseSseEvent(chatResponse);

          if (responseSseEvent != null) {
            LOGGER.info("Sending event '{}'", responseSseEvent.event());
          }

          return responseSseEvent;
        }).doOnNext(chatResponse -> {
          LOGGER.info("doOnNext response: {}", chatResponse);
        })
        .doOnComplete(() -> {
          LOGGER.info("doOnComplete. message={}", assistantMessageContent);

          Mono.fromRunnable(() -> saveNewMessage(assistantId, threadId, MessageType.ASSISTANT,
                  assistantMessageContent.toString(), null))  // Wrap blocking call
              .subscribeOn(Schedulers.boundedElastic())  // Subscribe on separate thread pool
              .subscribe();  // Subscribe to start execution
        })
        .doOnError(throwable -> {
          LOGGER.error("doOnError: {}", throwable.getMessage());
        });
  }

  private ServerSentEvent<String> createResponseSseEvent(ChatResponse chatResponse) {
    String finishReason = chatResponse.getResult().getMetadata().getFinishReason();

    ServerSentEvent<String> event = null;

    if ("STOP".equals(finishReason)) {
      event = ServerSentEvent.<String>builder()
          .event("done")
          .build();
    } else {
      String content = chatResponse.getResult().getOutput().getContent();
      content = content.replace("\n", "\\n");
      event = ServerSentEvent.<String>builder()
          .event("thread.message.delta")
          .data(content)
          .build();
    }

    return event;
  }

  public MessageListParsedDto listMessages(String threadId,
      ListSearchParameters listSearchParameters) {

    List<MessageEntity> messageEntities = messageRepository.findByThreadId(threadId,
        Sort.by(Direction.ASC, "createdAt"));
    List<MessageDto> messageDtos = messageEntities.stream()
        .map(assistantMapper::toDto)
        .toList();

    Map<String, String> parsedMessages = new HashMap<>();
    messageDtos.forEach(message -> parsedMessages.put(message.id(), message.parsedContent()));

    return new MessageListParsedDto(messageDtos, parsedMessages);
  }

  @Transactional
  public ParsedMessageDto postProcessLastMessage(String threadId) {
    LOGGER.info("Post processing last message: {}", threadId);

    List<MessageEntity> lastMessage = messageRepository.findByThreadId(
        threadId,
        PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt"))
    ).getContent();

    if (lastMessage.isEmpty()) {
      throw new AssistentException("No messages found for thread: " + threadId);
    }

    MessageEntity messageToProcess = lastMessage.getFirst();

    MessageEntity newMessageEntity;
    if (!lastMessage.isEmpty()) {
      newMessageEntity = messageToProcess;
    } else {
      newMessageEntity = new MessageEntity();
      newMessageEntity.setId(messageToProcess.getId());
      newMessageEntity.setParsedContent("");
    }

    String transformed = messageProcessor.transform(
        messageToProcess.getRawContent(),
        threadId, messageToProcess.getId());
    newMessageEntity.setParsedContent(transformed);

    messageRepository.save(newMessageEntity);

    ParsedMessageDto parsedMessageDto = new ParsedMessageDto();
    parsedMessageDto.setMessage(assistantMapper.toDto(newMessageEntity));
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

    String parsedTitle = generatedTitle.replace("\"", "");
    threadEntity.setTitle(parsedTitle);
    threadRepository.save(threadEntity);

    return new ThreadTitleDto(generatedTitle);
  }

  public ThreadDto retrieveThread(String threadId) {
    return threadRepository.findById(threadId)
        .map(assistantMapper::toDto)
        .orElseThrow(() -> new AssistentException("Thread not found"));
  }

  @Transactional
  public void modifyAssistant(String assistantId, AssistantDto modifiedAssistant) {
    AssistantEntity assistantEntity = assistantRepository.findById(assistantId)
        .orElseThrow(() -> new AssistentException("Assistant entity not found"));

    AssistantEntity modifiedEntity = new AssistantEntity();
    modifiedEntity.setId(assistantEntity.getId());
    modifiedEntity.setName(modifiedAssistant.name());
    modifiedEntity.setDescription(modifiedAssistant.description());
    modifiedEntity.setModel(modifiedAssistant.model());
    modifiedEntity.setInstructions(modifiedAssistant.instructions());
    modifiedEntity.setImagePath(modifiedAssistant.imagePath());
    modifiedEntity.setSystem(modifiedAssistant.system().name());
    modifiedEntity.setProperties(assistantMapper.mapProperties(modifiedAssistant.properties()));

    assistantRepository.save(modifiedEntity);
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

  @Transactional
  public AssistantDto createAssistant(AssistantDto assistant) {
    AssistantEntity assistantEntity = assistantMapper.toEntity(assistant);
    assistantEntity.setId(uniqueIdGenerator.generateAssistantId());
    return assistantMapper.toDto(assistantRepository.save(assistantEntity));
  }

  public List<String> retrieveModels() {
    return openAiService.listModels().stream()
        .map(Model::getId)
        .filter(id -> id.startsWith("gpt") && !id.contains("instruct"))
        .toList();
  }

  @Transactional
  public void deleteAssistant(String assistantId) {
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

  private MessageEntity saveNewMessage(String assistantId, String threadId, MessageType role,
      String rawContent) {
    return saveNewMessage(assistantId, threadId, role, rawContent);
  }

  private MessageEntity saveNewMessage(String assistantId, String threadId, MessageType role,
      String rawContent, String parsedContent) {
    LOGGER.debug("Saving new message with role={}, assistantId={}, threadId={}: {}", assistantId,
        threadId, role, rawContent);

    ThreadEntity threadEntity = threadRepository.findById(threadId)
        .orElseThrow(() -> new AssistentException("Thread not found"));
    AssistantEntity assistantEntity = assistantRepository.findById(assistantId)
        .orElseThrow(() -> new AssistentException("Assistant not found"));

    MessageEntity messageEntity = new MessageEntity();
    messageEntity.setId(uniqueIdGenerator.generateMessageId());
    messageEntity.setThread(threadEntity);
    messageEntity.setAssistant(assistantEntity);
    messageEntity.setRawContent(rawContent);
    messageEntity.setParsedContent(parsedContent);
    messageEntity.setRole(role.getValue());
    messageEntity.setCreatedAt(new Date());

    return messageRepository.save(messageEntity);
  }

  private OpenAiChatOptions getPromptOptions(AssistantDto assistantDto) {
    return OpenAiChatOptions.builder()
        .withModel(assistantDto.model())
        .build();
  }

  private String printPromptOptions(ModelSystem system, ChatOptions options) {
    StringBuilder printedOptions = new StringBuilder("[");
    printedOptions.append("system=").append(system).append(", ");

    if (options instanceof OpenAiChatOptions openAiChatOptions) {
      printedOptions.append("model=").append(openAiChatOptions.getModel()).append(", ");
      printedOptions.append("topP=").append(openAiChatOptions.getTopP()).append(", ");
      printedOptions.append("n=").append(openAiChatOptions.getN()).append(", ");
      printedOptions.append("seed=").append(openAiChatOptions.getSeed()).append(", ");
      printedOptions.append("frequencePenalty=").append(openAiChatOptions.getFrequencyPenalty())
          .append(", ");
      printedOptions.append("presencePenalty=").append(openAiChatOptions.getPresencePenalty())
          .append(", ");
      printedOptions.append("temperature=").append(openAiChatOptions.getTemperature()).append(", ");
    }

    printedOptions.append("]");
    return printedOptions.toString();
  }

}
