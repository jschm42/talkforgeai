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

package com.talkforgeai.backend.assistant;

import com.talkforgeai.backend.assistant.dto.ThreadCreateResponse;
import com.talkforgeai.service.openai.assistant.OpenAIAssistantService;
import com.talkforgeai.service.openai.assistant.dto.*;
import org.springframework.stereotype.Service;

@Service
public class AssistantService {

    private final OpenAIAssistantService assistantService;

    public AssistantService(OpenAIAssistantService assistantService) {
        this.assistantService = assistantService;
    }

    public ThreadCreateResponse createThread() {
        CreateThreadResponse thread = this.assistantService.createThread();
        return new ThreadCreateResponse(thread.id());
    }

    public PostMessageResponse postMessage(String threadId, PostMessageRequest postMessageRequest) {
        return this.assistantService.postMessage(threadId, postMessageRequest);
    }

    public Run runConversation(String threadId, RunConversationRequest runConversationRequest) {
        return this.assistantService.runConversation(threadId, runConversationRequest);
    }

    public ListMessageResponse listMessages(String threadId, ListMessagesRequest listMessagesRequest) {
        return this.assistantService.listMessages(threadId, listMessagesRequest);
    }

    public Run retrieveRun(String threadId, String runId) {
        return this.assistantService.retrieveRun(threadId, runId);
    }
}
