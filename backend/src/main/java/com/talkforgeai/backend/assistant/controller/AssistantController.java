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

import com.talkforgeai.backend.assistant.dto.*;
import com.talkforgeai.backend.assistant.service.AssistantService;
import com.talkforgeai.service.openai.assistant.dto.*;
import jakarta.websocket.server.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AssistantController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssistantController.class);

    private final AssistantService assistantService;

    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @GetMapping("/assistants/{assistantId}")
    public Assistant retrieveAssistant(@PathVariable("assistantId") String assistantId) {
        return assistantService.retrieveAssistant(assistantId);
    }

    @GetMapping("/assistants")
    public AssistantListDto listAssistants(@PathParam("limit") Integer limit, @PathParam("order") String order) {
        return assistantService.listAssistants(new ListRequest(limit, order));
    }

    @PostMapping("/assistants/sync")
    public void syncAssistants() {
        assistantService.syncAssistants();
    }

    @GetMapping("/threads")
    public List<ThreadDto> listThreads() {
        return assistantService.retrieveThreads();
    }

    @PostMapping("/threads")
    public ThreadDto createThread() {
        return assistantService.createThread();
    }

    @GetMapping("/threads/{threadId}")
    public ThreadDto retrieveThread(@PathVariable("threadId") String threadId) {
        return assistantService.retrieveThread(threadId);
    }

    @PostMapping("/threads/{threadId}/messages")
    public Message postMessage(@PathVariable("threadId") String threadId, @RequestBody PostMessageRequest postMessageRequest) {
        return assistantService.postMessage(threadId, postMessageRequest);
    }

    @GetMapping("/threads/{threadId}/messages")
    public MessageListParsedDto listMessages(@PathVariable("threadId") String threadId, @PathParam("limit") Integer limit, @PathParam("order") String order) {
        return assistantService.listMessages(threadId, new ListRequest(limit, order));
    }

    @PostMapping("/threads/{threadId}/runs")
    public Run runConversation(@PathVariable("threadId") String threadId, @RequestBody RunConversationRequest runConversationRequest) {
        return assistantService.runConversation(threadId, runConversationRequest);
    }

    @GetMapping("/threads/{threadId}/runs/{runId}")
    public Run getRun(@PathVariable("threadId") String threadId, @PathVariable("runId") String runId) {
        return assistantService.retrieveRun(threadId, runId);
    }

    @PostMapping("/threads/{threadId}/messages/{messageId}/postprocess")
    public ParsedMessageDto postProcessMessage(@PathVariable("threadId") String threadId, @PathVariable("messageId") String messageId) {
        return assistantService.postProcessMessage(threadId, messageId);
    }


    @GetMapping("/threads/{threadId}/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String threadId, @PathVariable String filename) {

        try {
            byte[] imageBytes = assistantService.getImage(threadId, filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageBytes);
        } catch (IOException ioException) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/threads/{threadId}/title/generate")
    @ResponseBody
    public ThreadTitleDto generateThreadTitle(@PathVariable("threadId") String threadId, @RequestBody ThreadTitleRequestDto request) {
        return assistantService.generateThreadTitle(threadId, request);
    }
}
