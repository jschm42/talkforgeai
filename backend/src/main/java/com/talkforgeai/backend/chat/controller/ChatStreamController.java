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

package com.talkforgeai.backend.chat.controller;

import com.talkforgeai.backend.chat.dto.ChatCompletionRequest;
import com.talkforgeai.backend.chat.service.ChatStreamService;
import com.talkforgeai.service.openai.dto.OpenAIChatStreamResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/chat/stream")
public class ChatStreamController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatStreamController.class);
    private final ChatStreamService chatStreamService;

    public ChatStreamController(ChatStreamService chatStreamService) {
        this.chatStreamService = chatStreamService;
    }

    @PostMapping("/submit")
    @ResponseBody
    public Flux<ServerSentEvent<OpenAIChatStreamResponse.StreamResponseChoice>> submit(@RequestBody ChatCompletionRequest request) {
        return chatStreamService.submit(request);
    }
}
