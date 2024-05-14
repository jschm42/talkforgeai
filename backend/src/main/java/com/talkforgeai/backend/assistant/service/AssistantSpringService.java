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
import com.talkforgeai.backend.assistant.dto.ImageGenSystem;
import com.talkforgeai.backend.assistant.dto.LlmSystem;
import com.talkforgeai.backend.assistant.dto.MessageDto;
import com.talkforgeai.backend.assistant.dto.MessageListParsedDto;
import com.talkforgeai.backend.assistant.dto.ModelSystemInfo;
import com.talkforgeai.backend.assistant.dto.ParsedMessageDto;
import com.talkforgeai.backend.assistant.dto.ProfileImageUploadResponse;
import com.talkforgeai.backend.assistant.dto.ThreadDto;
import com.talkforgeai.backend.assistant.dto.ThreadTitleDto;
import com.talkforgeai.backend.assistant.dto.ThreadTitleGenerationRequestDto;
import com.talkforgeai.backend.assistant.dto.ThreadTitleUpdateRequestDto;
import com.talkforgeai.backend.assistant.exception.AssistentException;
import com.talkforgeai.backend.assistant.functions.ContextStorageFunction;
import com.talkforgeai.backend.assistant.functions.ContextStorageFunction.Request;
import com.talkforgeai.backend.assistant.functions.ContextStorageFunction.Response;
import com.talkforgeai.backend.assistant.functions.ContextTool;
import com.talkforgeai.backend.assistant.functions.FunctionContext;
import com.talkforgeai.backend.assistant.repository.AssistantRepository;
import com.talkforgeai.backend.assistant.repository.MessageRepository;
import com.talkforgeai.backend.assistant.repository.ThreadRepository;
import com.talkforgeai.backend.memory.dto.DocumentWithoutEmbeddings;
import com.talkforgeai.backend.memory.service.MemoryService;
import com.talkforgeai.backend.service.UniqueIdGenerator;
import com.talkforgeai.backend.storage.FileStorageService;
import com.talkforgeai.backend.transformers.MessageProcessor;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.autoconfigure.openai.OpenAiEmbeddingProperties;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi.ChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
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

@Service
public class AssistantSpringService {

  public static final Logger LOGGER = LoggerFactory.getLogger(AssistantSpringService.class);
  public static final String SYSTEM_MESSAGE_PLANTUML = "You can generate PlantUML diagrams. PlantUML code that you generate will be transformed to a downloadable image.";
  public static final String SYSTEM_MESSAGE_IMAGE_GEN = "You can generate an image by using the following syntax: !image_gen[<image prompt>]";

  private final UniversalChatService universalChatService;
  private final UniversalImageGenService universalImageGenService;

  private final AssistantRepository assistantRepository;
  private final MessageRepository messageRepository;
  private final ThreadRepository threadRepository;

  private final FileStorageService fileStorageService;

  private final MessageProcessor messageProcessor;

  private final AssistantMapper assistantMapper;

  private final UniqueIdGenerator uniqueIdGenerator;

  private final MemoryService memoryService;


  private final Map<String, Subscription> activeStreams = new ConcurrentHashMap<>();

  public AssistantSpringService(
      UniversalChatService universalChatService, UniversalImageGenService universalImageGenService,
      AssistantRepository assistantRepository, MessageRepository messageRepository,
      ThreadRepository threadRepository, FileStorageService fileStorageService,
      MessageProcessor messageProcessor, AssistantMapper assistantMapper,
      UniqueIdGenerator uniqueIdGenerator, MemoryService memoryService) {

    this.universalChatService = universalChatService;
    this.universalImageGenService = universalImageGenService;
    this.assistantRepository = assistantRepository;
    this.messageRepository = messageRepository;
    this.threadRepository = threadRepository;
    this.fileStorageService = fileStorageService;
    this.messageProcessor = messageProcessor;
    this.assistantMapper = assistantMapper;
    this.uniqueIdGenerator = uniqueIdGenerator;
    this.memoryService = memoryService;
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

  public boolean doesAssistantExistByName(String assistantName) {
    return assistantRepository.existsByName(assistantName);
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

  public void cancelStream(String threadId, String runId) {
    LOGGER.info("Cancelling stream for threadId={}, runId={}", threadId, runId);

    Subscription subscription = activeStreams.remove(runId);
    if (subscription != null) {
      subscription.cancel();
    }
  }

  public Flux<ServerSentEvent<String>> streamRunConversation(String assistantId, String threadId,
      String message) {

    final String runId = uniqueIdGenerator.generateRunId();

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

    Mono<List<DocumentWithoutEmbeddings>> memoryResults = Mono.fromCallable(
            () -> memoryService.search(SearchRequest.query(message).withSimilarityThreshold(0.75f)))
        .subscribeOn(Schedulers.boundedElastic());

    Mono<PreparedInfos> preparedInfosMono = Mono.zip(
        Arrays.asList(assistantEntityMono, pastMessages, memoryResults), args -> {
          return new PreparedInfos(
              (AssistantDto) args[0],
              (List<MessageDto>) args[1],
              (List<DocumentWithoutEmbeddings>) args[2]
          );
        });

    Flux<ServerSentEvent<String>> runIdMono = Flux.just(ServerSentEvent.<String>builder()
        .event("run.started")
        .data(runId)
        .build());

    StringBuilder assistantMessageContent = new StringBuilder();

    return runIdMono.concatWith(
        saveUserMessageMono
            .then(assistantEntityMono)
            .then(preparedInfosMono)
            .flux()
            .flatMap(preparedInfos -> {
              AssistantDto assistantDto = preparedInfos.assistantDto();
              List<MessageDto> pastMessagesList = preparedInfos.pastMessages();
              List<DocumentWithoutEmbeddings> memoryResultsList = preparedInfos.memoryResults();

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

              if (assistantDto.properties()
                  .get(AssistantProperties.FEATURE_IMAGEGENERATION.getKey()).equals(
                      "true")) {
                finalPromptMessageList.addFirst(new SystemMessage(SYSTEM_MESSAGE_IMAGE_GEN));
              }

              if (assistantDto.properties()
                  .get(AssistantProperties.FEATURE_PLANTUML.getKey()).equals(
                      "true")) {
                finalPromptMessageList.addFirst(new SystemMessage(SYSTEM_MESSAGE_PLANTUML));
              }

              finalPromptMessageList.addFirst(new SystemMessage(assistantDto.instructions()));

              StringBuilder memoryMessage = new StringBuilder();
              if (!memoryResultsList.isEmpty()) {
                memoryMessage.append("Use the following information from memory:\n");
                memoryResultsList.forEach(
                    result -> memoryMessage.append(result.content()).append("\n"));
                memoryMessage.append("\nUser message:\n");
              }

              finalPromptMessageList.add(new UserMessage(memoryMessage.append(message).toString()));

              FunctionCallbackWrapper<Request, Response> func = FunctionCallbackWrapper.builder(
                      new ContextStorageFunction(memoryService, new FunctionContext(LlmSystem.OPENAI,
                          OpenAiEmbeddingProperties.DEFAULT_EMBEDDING_MODEL, assistantId,
                          assistantDto.name())))
                  .withDescription(
                      "Store relevant information in the vector database for later retrieval.")
                  .withName(ContextTool.MEMORY_STORE.getFunctionBeanName())
                  .build();

              ChatOptions promptOptions = universalChatService.getPromptOptions(assistantDto,
                  List.of(func));

              LOGGER.debug("Starting stream with prompt: {}", finalPromptMessageList);
              LOGGER.debug("Prompt Options: {}",
                  universalChatService.printPromptOptions(assistantDto.system(), promptOptions));

              Prompt prompt = new Prompt(finalPromptMessageList, promptOptions);

              return universalChatService.stream(assistantDto.system(), prompt);
            })
            .doOnCancel(() -> {
              LOGGER.debug("doOnCancel. message={}", assistantMessageContent);
            })
            .mapNotNull(chatResponse -> {
              String content = chatResponse.getResult().getOutput().getContent();
              LOGGER.debug("ChatResponse received: {}", content);

              if (content != null) {
                assistantMessageContent.append(chatResponse.getResult().getOutput().getContent());
              }

              ServerSentEvent<String> responseSseEvent = createResponseSseEvent(chatResponse);

              if (responseSseEvent != null) {
                LOGGER.debug("Sending event '{}'", responseSseEvent.event());
              }

              return responseSseEvent;
            })
            .doOnSubscribe(subscription -> {
              LOGGER.debug("doOnSubscribe. message={}", assistantMessageContent);

              activeStreams.put(runId, subscription);
            })
            .doOnNext(chatResponse -> {
              LOGGER.debug("doOnNext response: {}", chatResponse);
            })
            .doOnComplete(() -> {
              LOGGER.debug("doOnComplete. message={}", assistantMessageContent);

              Mono.fromRunnable(() -> saveNewMessage(assistantId, threadId, MessageType.ASSISTANT,
                      assistantMessageContent.toString(), null))  // Wrap blocking call
                  .subscribeOn(Schedulers.boundedElastic())  // Subscribe on separate thread pool
                  .subscribe();  // Subscribe to start execution
            })
            .onErrorResume(throwable -> {
              LOGGER.error("Error while streaming: {}", throwable.getMessage());
              return Flux.just(ServerSentEvent.<String>builder()
                  .event("error")
                  .data(throwable.getMessage())
                  .build());
            }));
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
      String eventContent = "";

      if (content != null && !content.isEmpty()) {
        eventContent = content.replace("\n", "\\n");
      }

      event = ServerSentEvent.<String>builder()
          .event("thread.message.delta")
          .data(eventContent)
          .build();
    }

    return event;
  }

  public MessageListParsedDto listMessages(String threadId) {

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
        """.formatted(request.userMessage());

    OpenAiChatOptions options = OpenAiChatOptions.builder()
        .withModel(ChatModel.GPT_3_5_TURBO.getValue())
        .withMaxTokens(256)
        .build();

    Prompt titlePrompt = new Prompt(new UserMessage(content), options);

    try {
      ChatResponse titleResponse = universalChatService.call(LlmSystem.OPENAI, titlePrompt);

      String generatedTitle = titleResponse.getResult().getOutput().getContent();

      String parsedTitle = generatedTitle.replace("\"", "");
      threadEntity.setTitle(parsedTitle);
      threadRepository.save(threadEntity);
      return new ThreadTitleDto(generatedTitle);
    } catch (Exception e) {
      throw new AssistentException("Failed to generate title", e);
    }
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
    modifiedEntity.setProperties(new HashMap<>());
    AssistantEntity savedAssistant = assistantRepository.save(modifiedEntity);

    savedAssistant.setProperties(assistantMapper.mapProperties(modifiedAssistant.properties()));
    assistantRepository.save(savedAssistant);
  }

  public GenerateImageResponse generateImage(String prompt) throws IOException {
    ImageResponse imageResponse = universalImageGenService.generate(ImageGenSystem.OPENAI, prompt);
    return new GenerateImageResponse(downloadImage(imageResponse.getResult().getOutput().getUrl()));
  }

  @Transactional
  public AssistantDto createAssistant(AssistantDto assistant) {
    AssistantEntity assistantEntity = assistantMapper.toEntity(assistant);
    assistantEntity.setId(uniqueIdGenerator.generateAssistantId());
    return assistantMapper.toDto(assistantRepository.save(assistantEntity));
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

  public List<ModelSystemInfo> listSystems() {
    return Stream.of(LlmSystem.values())
        .map(system -> new ModelSystemInfo(system.name(), system.getDescription()))
        .toList();
  }

  public List<String> retrieveModels(LlmSystem system) {
    return universalChatService.getModels(system);
  }

  record PreparedInfos(AssistantDto assistantDto, List<MessageDto> pastMessages,
                       List<DocumentWithoutEmbeddings> memoryResults) {

  }
}
