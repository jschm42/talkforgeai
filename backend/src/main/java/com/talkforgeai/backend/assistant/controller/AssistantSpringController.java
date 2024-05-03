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
import com.talkforgeai.backend.assistant.dto.ChatCompletionSpringRequest;
import com.talkforgeai.backend.assistant.dto.GenerateImageRequest;
import com.talkforgeai.backend.assistant.dto.GenerateImageResponse;
import com.talkforgeai.backend.assistant.dto.LlmSystem;
import com.talkforgeai.backend.assistant.dto.MessageListParsedDto;
import com.talkforgeai.backend.assistant.dto.ModelSystemInfo;
import com.talkforgeai.backend.assistant.dto.ParsedMessageDto;
import com.talkforgeai.backend.assistant.dto.ProfileImageUploadResponse;
import com.talkforgeai.backend.assistant.dto.ThreadDto;
import com.talkforgeai.backend.assistant.dto.ThreadTitleDto;
import com.talkforgeai.backend.assistant.dto.ThreadTitleGenerationRequestDto;
import com.talkforgeai.backend.assistant.dto.ThreadTitleUpdateRequestDto;
import com.talkforgeai.backend.assistant.service.AssistantSpringService;
import com.talkforgeai.backend.storage.FileStorageService;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1")
public class AssistantSpringController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssistantSpringController.class);

  private final AssistantSpringService assistantService;

  private final FileStorageService fileStorageService;

  public AssistantSpringController(AssistantSpringService assistantService,
      FileStorageService fileStorageService) {
    this.assistantService = assistantService;
    this.fileStorageService = fileStorageService;
  }

  @GetMapping("/assistants/{assistantId}")
  public AssistantDto retrieveAssistant(@PathVariable("assistantId") String assistantId) {
    return assistantService.retrieveAssistant(assistantId);
  }

  @GetMapping("/assistants")
  public List<AssistantDto> listAssistants(@PathParam("limit") Integer limit,
      @PathParam("order") String order) {
    return assistantService.listAssistants(limit, order);
  }

  @DeleteMapping("/assistants/{assistantId}")
  public void deleteAssistant(@PathVariable("assistantId") String assistantId) {
    assistantService.deleteAssistant(assistantId);
  }

  @PostMapping("/assistants/{assistantId}")
  public void modifyAssistant(@PathVariable("assistantId") String assistantId,
      @RequestBody AssistantDto modifiedAssistant) {
    assistantService.modifyAssistant(assistantId, modifiedAssistant);
  }

  @PostMapping("/assistants")
  public AssistantDto createAssistant(@RequestBody AssistantDto assistant) {
    return assistantService.createAssistant(assistant);
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
  public ProfileImageUploadResponse singleImageUpload(@RequestParam("file") MultipartFile file) {
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

  @GetMapping("/threads/{threadId}/messages")
  public MessageListParsedDto listMessages(@PathVariable("threadId") String threadId,
      @PathParam("limit") Integer limit, @PathParam("order") String order) {

    return assistantService.listMessages(threadId);
  }


  @PostMapping("/threads/{threadId}/runs/stream")
  public Flux<ServerSentEvent<String>> runStreamConversation(
      @PathVariable("threadId") String threadId,
      @RequestBody ChatCompletionSpringRequest request) {
    return assistantService.streamRunConversation(request.assistantId(), threadId,
        request.message());
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
  public void deleteThread(@PathVariable("threadId") String threadId) {
    assistantService.deleteThread(threadId);
  }

  @PostMapping("/threads/{threadId}/title")
  public ThreadTitleDto updateThreadTitle(@PathVariable("threadId") String threadId,
      @RequestBody ThreadTitleUpdateRequestDto request) {
    return assistantService.updateThreadTitle(threadId, request);
  }

  @PostMapping("/threads/{threadId}/title/generate")
  public ThreadTitleDto generateThreadTitle(@PathVariable("threadId") String threadId,
      @RequestBody ThreadTitleGenerationRequestDto request) {
    return assistantService.generateThreadTitle(threadId, request);
  }

  @PostMapping("/threads/{threadId}/runs/{runId}/cancel")
  public ResponseEntity<Void> cancelRun(@PathVariable String threadId, @PathVariable String runId) {
    assistantService.cancelStream(threadId, runId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/assistants/models/{llmSystem}")
  public List<String> retrieveAssistantModels(
      @PathVariable("llmSystem") LlmSystem llmSystem) {
    return assistantService.retrieveModels(llmSystem);
  }

  @GetMapping("/assistants/systems")
  public List<ModelSystemInfo> listSystems() {
    return assistantService.listSystems();
  }
}
