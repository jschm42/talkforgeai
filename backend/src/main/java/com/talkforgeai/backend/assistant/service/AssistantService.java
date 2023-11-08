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

import com.talkforgeai.backend.assistant.domain.MessageEntity;
import com.talkforgeai.backend.assistant.domain.ThreadEntity;
import com.talkforgeai.backend.assistant.dto.ParsedMessageDto;
import com.talkforgeai.backend.assistant.dto.ThreadDto;
import com.talkforgeai.backend.assistant.repository.AssistantRepository;
import com.talkforgeai.backend.assistant.repository.MessageRepository;
import com.talkforgeai.backend.assistant.repository.ThreadRepository;
import com.talkforgeai.backend.transformers.MessageProcessor;
import com.talkforgeai.service.openai.assistant.OpenAIAssistantService;
import com.talkforgeai.service.openai.assistant.dto.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssistantService {

    private final OpenAIAssistantService assistantService;
    private final AssistantRepository assistantRepository;
    private final MessageRepository messageRepository;
    private final ThreadRepository threadRepository;

    private final MessageProcessor messageProcessor;

    public AssistantService(OpenAIAssistantService assistantService, AssistantRepository assistantRepository, MessageRepository messageRepository, ThreadRepository threadRepository, MessageProcessor messageProcessor) {
        this.assistantService = assistantService;
        this.assistantRepository = assistantRepository;
        this.messageRepository = messageRepository;
        this.threadRepository = threadRepository;
        this.messageProcessor = messageProcessor;
    }

    public Assistant retrieveAssistant(String assistantId) {
        return this.assistantService.retrieveAssistant(assistantId);
    }

    public ListAssistantResponse listAssistants(ListRequest listAssistantsRequest) {
        return this.assistantService.listAssistants(listAssistantsRequest);
    }

    @Transactional
    public ThreadDto createThread() {
        CreateThreadResponse thread = this.assistantService.createThread();

        ThreadEntity threadEntity = new ThreadEntity();
        threadEntity.setId(thread.id());
        threadEntity.setTitle("<no title>");
        threadEntity.setCreatedAt(thread.createdAt());
        threadRepository.save(threadEntity);

        return mapToDto(threadEntity);
    }

    public List<ThreadDto> retrieveThreads() {
        return this.threadRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    public Message postMessage(String threadId, PostMessageRequest postMessageRequest) {
        return this.assistantService.postMessage(threadId, postMessageRequest);
    }

    public Run runConversation(String threadId, RunConversationRequest runConversationRequest) {
        return this.assistantService.runConversation(threadId, runConversationRequest);
    }

    public ListMessageResponse listMessages(String threadId, ListRequest listMessagesRequest) {
        return this.assistantService.listMessages(threadId, listMessagesRequest);
    }

    public Run retrieveRun(String threadId, String runId) {
        return this.assistantService.retrieveRun(threadId, runId);
    }

    private ThreadDto mapToDto(ThreadEntity threadEntity) {
        return new ThreadDto(threadEntity.getId(), threadEntity.getTitle(), threadEntity.getCreatedAt());
    }

    @Transactional
    public ParsedMessageDto postProcessMessage(String threadId, String messageId) {
        Message message = this.assistantService.retrieveMessage(threadId, messageId);
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
}
