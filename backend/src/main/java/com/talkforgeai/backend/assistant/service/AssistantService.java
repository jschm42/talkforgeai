/*
 * Copyright (c) 2023 Jean Schmitz.
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
import com.talkforgeai.backend.assistant.dto.*;
import com.talkforgeai.backend.assistant.exceptions.AssistentException;
import com.talkforgeai.backend.assistant.repository.AssistantRepository;
import com.talkforgeai.backend.assistant.repository.MessageRepository;
import com.talkforgeai.backend.assistant.repository.ThreadRepository;
import com.talkforgeai.backend.persona.controller.GenerateImageResponse;
import com.talkforgeai.backend.session.exception.SessionException;
import com.talkforgeai.backend.storage.FileStorageService;
import com.talkforgeai.backend.transformers.MessageProcessor;
import com.talkforgeai.service.openai.OpenAIChatService;
import com.talkforgeai.service.openai.OpenAIImageService;
import com.talkforgeai.service.openai.assistant.OpenAIAssistantService;
import com.talkforgeai.service.openai.assistant.dto.Thread;
import com.talkforgeai.service.openai.assistant.dto.*;
import com.talkforgeai.service.openai.dto.*;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class AssistantService {
    public static final Logger LOGGER = LoggerFactory.getLogger(AssistantService.class);

    private final OpenAIAssistantService openAIAssistantService;

    private final OpenAIChatService openAIChatService;
    private final OpenAIImageService openAIImageService;
    private final AssistantRepository assistantRepository;
    private final MessageRepository messageRepository;
    private final ThreadRepository threadRepository;

    private final FileStorageService fileStorageService;

    private final MessageProcessor messageProcessor;

    private final AssistantMapper assistantMapper;

    public AssistantService(OpenAIAssistantService openAIAssistantService, OpenAIChatService openAIChatService, OpenAIImageService openAIImageService, AssistantRepository assistantRepository, MessageRepository messageRepository, ThreadRepository threadRepository, FileStorageService fileStorageService, MessageProcessor messageProcessor, AssistantMapper assistantMapper) {
        this.openAIAssistantService = openAIAssistantService;
        this.openAIChatService = openAIChatService;
        this.openAIImageService = openAIImageService;
        this.assistantRepository = assistantRepository;
        this.messageRepository = messageRepository;
        this.threadRepository = threadRepository;
        this.fileStorageService = fileStorageService;
        this.messageProcessor = messageProcessor;
        this.assistantMapper = assistantMapper;
    }

    public AssistantDto retrieveAssistant(String assistantId) {
        Assistant assistant = this.openAIAssistantService.retrieveAssistant(assistantId);

        if (assistant != null) {
            Optional<AssistantEntity> assistantEntity = assistantRepository.findById(assistant.id());

            if (assistantEntity.isPresent()) {
                return assistantMapper.mapAssistantDto(assistant, assistantEntity.get());
            }
        }

        return null;
    }

    public List<AssistantDto> listAssistants(ListRequest listAssistantsRequest) {
        AssistantList assistantList = this.openAIAssistantService.listAssistants(listAssistantsRequest);

        List<AssistantDto> assistantDtoList = new ArrayList<>();


        assistantList.data().forEach(assistant -> {
            Optional<AssistantEntity> assistantEntity = assistantRepository.findById(assistant.id());

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
        AssistantList assistantList = this.openAIAssistantService.listAssistants(new ListRequest());
        List<AssistantEntity> assistantEntities = assistantRepository.findAll();

        assistantList.data().forEach(assistant -> {
            LOGGER.info("Syncing assistant: {}", assistant.id());

            Optional<AssistantEntity> assistantEntity = assistantEntities.stream()
                    .filter(entity -> entity.getId().equals(assistant.id()))
                    .findFirst();

            if (assistantEntity.isEmpty()) {
                LOGGER.info("New assistant detected. Creating entity: {}", assistant.id());

                AssistantEntity entity = new AssistantEntity();
                entity.setId(assistant.id());

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
    }

    @Transactional
    public ThreadDto createThread() {
        Thread thread = this.openAIAssistantService.createThread();

        ThreadEntity threadEntity = new ThreadEntity();
        threadEntity.setId(thread.id());
        threadEntity.setTitle("<no title>");
        threadEntity.setCreatedAt(thread.createdAt());
        threadRepository.save(threadEntity);

        return mapToDto(threadEntity);
    }

    public List<ThreadDto> retrieveThreads() {
        return this.threadRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(this::mapToDto)
                .toList();
    }

    public Message postMessage(String threadId, PostMessageRequest postMessageRequest) {
        return this.openAIAssistantService.postMessage(threadId, postMessageRequest);
    }

    public Run runConversation(String threadId, RunConversationRequest runConversationRequest) {
        return this.openAIAssistantService.runConversation(threadId, runConversationRequest);
    }

    public MessageListParsedDto listMessages(String threadId, ListRequest listMessagesRequest) {
        MessageList messageList = this.openAIAssistantService.listMessages(threadId, listMessagesRequest);
        Map<String, String> parsedMessages = new HashMap<>();

        messageList.data().forEach(message -> {
            Optional<MessageEntity> messageEntity = messageRepository.findById(message.id());
            messageEntity.ifPresent(entity -> parsedMessages.put(message.id(), entity.getParsedContent()));
        });

        return new MessageListParsedDto(messageList, parsedMessages);
    }

    public Run retrieveRun(String threadId, String runId) {
        return this.openAIAssistantService.retrieveRun(threadId, runId);
    }

    private ThreadDto mapToDto(ThreadEntity threadEntity) {
        return new ThreadDto(threadEntity.getId(), threadEntity.getTitle(), threadEntity.getCreatedAt());
    }

    @Transactional
    public ParsedMessageDto postProcessMessage(String threadId, String messageId) {
        Message message = this.openAIAssistantService.retrieveMessage(threadId, messageId);
        Optional<MessageEntity> messageEntity = messageRepository.findById(messageId);

        MessageEntity newMessageEntity = null;
        if (messageEntity.isPresent()) {
            newMessageEntity = messageEntity.get();
        } else {
            newMessageEntity = new MessageEntity();
            newMessageEntity.setId(message.id());
            newMessageEntity.setParsedContent("");
        }

        String transformed = messageProcessor.transform(message.content().get(0).text().value(), threadId, messageId);
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
    public ThreadTitleDto generateThreadTitle(String threadId, ThreadTitleRequestDto request) {
        ThreadEntity threadEntity = threadRepository.findById(threadId).orElseThrow(() -> new SessionException("Thread not found"));

        OpenAIChatRequest titleRequest = getTitleRequest(request);

        OpenAIChatResponse response = openAIChatService.submit(titleRequest);
        String generatedTitle = response.choices().get(0).message().content();

        String parsedTitle = generatedTitle.replaceAll("\"", "");
        threadEntity.setTitle(parsedTitle);
        threadRepository.save(threadEntity);

        return new ThreadTitleDto(generatedTitle);
    }

    @NotNull
    private OpenAIChatRequest getTitleRequest(ThreadTitleRequestDto request) {
        OpenAIChatRequest titleRequest = new OpenAIChatRequest();
        titleRequest.setModel("gpt-3.5-turbo");
        titleRequest.setMaxTokens(20);
        titleRequest.setTemperature(0.5);

        String content = """
                Generate a title in less than 6 words for the following message: %s
                """.formatted(request.userMessageContent());

        OpenAIChatMessage titleMessage = new OpenAIChatMessage(OpenAIChatMessage.Role.USER, content);
        titleRequest.setMessages(List.of(titleMessage));
        return titleRequest;
    }


    public ThreadDto retrieveThread(String threadId) {
        return threadRepository.findById(threadId)
                .map(this::mapToDto)
                .orElseThrow(() -> new SessionException("Thread not found"));
    }

    @Transactional
    public void modifyAssistant(String assistantId, AssistantDto modifiedAssistant) {
        Assistant assistant = openAIAssistantService.retrieveAssistant(assistantId);

        if (assistant == null) {
            throw new AssistentException("Assistant not found");
        }

        AssistantEntity assistantEntity = assistantRepository.findById(assistantId)
                .orElseThrow(() -> new AssistentException("Assistant entity not found"));

        assistantEntity.setImagePath(modifiedAssistant.imagePath());
        assistantEntity.setProperties(assistantMapper.mapProperties(modifiedAssistant.properties()));

        Assistant openAIModifiedAssistant = assistantMapper.mapAssistant(modifiedAssistant);

        assistantRepository.save(assistantEntity);
        openAIAssistantService.modifyAssistant(assistantId, openAIModifiedAssistant);
    }

    public GenerateImageResponse generateImage(String prompt) throws IOException {
        OpenAIImageRequest request = new OpenAIImageRequest(prompt, 1, "1024x1024");
        OpenAIImageResponse response = openAIImageService.submit(request);

        return new GenerateImageResponse(downloadImage(response.data().get(0).url()));
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


}
