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

package com.talkforgeai.backend.assistant.controller;

import com.talkforgeai.backend.assistant.AssistantService;
import com.talkforgeai.backend.assistant.dto.ThreadCreateResponse;
import com.talkforgeai.service.openai.assistant.dto.*;
import jakarta.websocket.server.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assistant")
public class AssistantController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssistantController.class);

    private final AssistantService assistantService;

    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping("/threads")
    public ThreadCreateResponse createThread() {
        return assistantService.createThread();
    }

    @PostMapping("/threads/{threadId}/messages")
    public PostMessageResponse postMessage(@PathVariable("threadId") String threadId, @RequestBody PostMessageRequest postMessageRequest) {
        return assistantService.postMessage(threadId, postMessageRequest);
    }

    @GetMapping("/threads/{threadId}/messages")
    public ListMessageResponse listMessages(@PathVariable("threadId") String threadId, @PathParam("limit") int limit, @PathParam("order") String order) {
        return assistantService.listMessages(threadId, new ListMessagesRequest(limit, order));
    }

    @PostMapping("/threads/{threadId}/runs")
    public Run runConversation(@PathVariable("threadId") String threadId, @RequestBody RunConversationRequest runConversationRequest) {
        return assistantService.runConversation(threadId, runConversationRequest);
    }

    @GetMapping("/threads/{threadId}/runs/{runId}")
    public Run getRun(@PathVariable("threadId") String threadId, @PathVariable("runId") String runId) {
        return assistantService.retrieveRun(threadId, runId);
    }

}
