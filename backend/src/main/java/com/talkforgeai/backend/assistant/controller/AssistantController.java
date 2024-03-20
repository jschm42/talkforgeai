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

package com.talkforgeai.backend.assistant.controller;

import com.talkforgeai.backend.assistant.dto.AssistantDto;
import com.talkforgeai.backend.assistant.dto.GenerateImageRequest;
import com.talkforgeai.backend.assistant.dto.GenerateImageResponse;
import com.talkforgeai.backend.assistant.dto.MessageListParsedDto;
import com.talkforgeai.backend.assistant.dto.ParsedMessageDto;
import com.talkforgeai.backend.assistant.dto.ProfileImageUploadResponse;
import com.talkforgeai.backend.assistant.dto.ThreadDto;
import com.talkforgeai.backend.assistant.dto.ThreadTitleDto;
import com.talkforgeai.backend.assistant.dto.ThreadTitleGenerationRequestDto;
import com.talkforgeai.backend.assistant.dto.ThreadTitleUpdateRequestDto;
import com.talkforgeai.backend.assistant.service.AssistantService;
import com.talkforgeai.backend.storage.FileStorageService;
import com.talkforgeai.service.openai.dto.StreamResponse;
import com.theokanning.openai.ListSearchParameters;
import com.theokanning.openai.ListSearchParameters.Order;
import com.theokanning.openai.messages.Message;
import com.theokanning.openai.messages.MessageRequest;
import com.theokanning.openai.runs.Run;
import com.theokanning.openai.runs.RunCreateRequest;
import jakarta.websocket.server.PathParam;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1")
public class AssistantController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssistantController.class);

  private final AssistantService assistantService;

  private final FileStorageService fileStorageService;

  public AssistantController(AssistantService assistantService,
      FileStorageService fileStorageService) {
    this.assistantService = assistantService;
    this.fileStorageService = fileStorageService;
  }

  @GetMapping("/assistants/models")
  public List<String> retrieveAssistantModelIds() {
    return assistantService.retrieveModels();
  }

  @GetMapping("/assistants/{assistantId}")
  public AssistantDto retrieveAssistant(@PathVariable("assistantId") String assistantId) {
    return assistantService.retrieveAssistant(assistantId);
  }

  @GetMapping("/assistants")
  public List<AssistantDto> listAssistants(@PathParam("limit") Integer limit,
      @PathParam("order") String order) {
    ListSearchParameters listRequest = ListSearchParameters.builder()
        .limit(limit)
        .order(Order.valueOf(order))
        .build();
    return assistantService.listAssistants(listRequest);
  }

  @DeleteMapping("/assistants/{assistantId}")
  public void deleteAssistant(@PathVariable("assistantId") String assistantId) {
    assistantService.deleteAssistant(assistantId);
  }

  @PostMapping("/assistants/sync")
  public ResponseEntity<String> syncAssistants() {
    try {
      assistantService.syncAssistants();
    } catch (RuntimeException e) {
      LOGGER.error("Error syncing assistants.", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(e.getMessage());
    }
    return ResponseEntity.ok().build();
  }

  @PostMapping("/assistants/{assistantId}")
  public void modifyAssistant(@PathVariable("assistantId") String assistantId,
      @RequestBody AssistantDto modifiedAssistant) {
    assistantService.modifyAssistant(assistantId, modifiedAssistant);
  }

  @PostMapping("/assistants")
  public AssistantDto createAssistant(@RequestBody AssistantDto modifiedAssistant) {
    return assistantService.createAssistant(modifiedAssistant);
  }

  @GetMapping("/assistants/images/{imageFile}")
  public ResponseEntity<byte[]> getImage(@PathVariable String imageFile) {
    try {
      Path imgFilePath = fileStorageService.getAssistantsDirectory().resolve(imageFile);
      Resource resource = new FileSystemResource(imgFilePath);
      byte[] imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());

      return ResponseEntity.ok()
          .contentType(MediaType.IMAGE_PNG)
          .body(imageBytes);
    } catch (IOException e) {
      LOGGER.error("Error loading image file: {}.", imageFile, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PostMapping("/assistants/images/upload")
  public ProfileImageUploadResponse singleFileUpload(@RequestParam("file") MultipartFile file) {
    return assistantService.uploadImage(file);
  }

  @PostMapping("/assistants/images/generate")
  public GenerateImageResponse generateImage(@RequestBody GenerateImageRequest generateImageRequest)
      throws IOException {
    return assistantService.generateImage(generateImageRequest.prompt());
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
  public Message postMessage(@PathVariable("threadId") String threadId,
      @RequestBody MessageRequest messageRequest) {
    return assistantService.postMessage(threadId, messageRequest);
  }

  @GetMapping("/threads/{threadId}/messages")
  public MessageListParsedDto listMessages(@PathVariable("threadId") String threadId,
      @PathParam("limit") Integer limit, @PathParam("order") String order) {

    ListSearchParameters listRequest = ListSearchParameters.builder()
        .limit(limit)
        .order(Order.valueOf(order))
        .build();
    return assistantService.listMessages(threadId, listRequest);
  }

  @PostMapping("/threads/{threadId}/runs")
  public Run runConversation(@PathVariable("threadId") String threadId,
      @RequestBody RunCreateRequest runConversationRequest) {
    return assistantService.runConversation(threadId, runConversationRequest);
  }

  @PostMapping("/threads/{threadId}/runs/stream")
  public Flux<ServerSentEvent<StreamResponse>> runStreamConversation(
      @PathVariable("threadId") String threadId,
      @RequestBody RunCreateRequest runConversationRequest) {
    return assistantService.streamRunConversation(threadId, runConversationRequest);
  }

  @GetMapping("/threads/{threadId}/runs/{runId}")
  public Run getRun(@PathVariable("threadId") String threadId,
      @PathVariable("runId") String runId) {
    return assistantService.retrieveRun(threadId, runId);
  }

  @PostMapping("/threads/{threadId}/runs/{runId}/cancel")
  public Run cancelRun(@PathVariable("threadId") String threadId,
      @PathVariable("runId") String runId) {
    return assistantService.cancelRun(threadId, runId);
  }

  @PostMapping("/threads/{threadId}/messages/last/postprocess")
  public ParsedMessageDto postProcessMessage(@PathVariable("threadId") String threadId) {
    return assistantService.postProcessLastMessage(threadId);
  }


  @GetMapping("/threads/{threadId}/{filename}")
  public ResponseEntity<byte[]> getImage(@PathVariable String threadId,
      @PathVariable String filename) {

    try {
      byte[] imageBytes = assistantService.getImage(threadId, filename);

      return ResponseEntity.ok()
          .contentType(MediaType.IMAGE_PNG)
          .body(imageBytes);
    } catch (IOException ioException) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @DeleteMapping("/threads/{threadId}")
  @ResponseBody
  public void deleteThread(@PathVariable("threadId") String threadId) {
    assistantService.deleteThread(threadId);
  }

  @PostMapping("/threads/{threadId}/title")
  @ResponseBody
  public ThreadTitleDto updateThreadTitle(@PathVariable("threadId") String threadId,
      @RequestBody ThreadTitleUpdateRequestDto request) {
    return assistantService.updateThreadTitle(threadId, request);
  }

  @PostMapping("/threads/{threadId}/title/generate")
  @ResponseBody
  public ThreadTitleDto generateThreadTitle(@PathVariable("threadId") String threadId,
      @RequestBody ThreadTitleGenerationRequestDto request) {
    return assistantService.generateThreadTitle(threadId, request);
  }
}
