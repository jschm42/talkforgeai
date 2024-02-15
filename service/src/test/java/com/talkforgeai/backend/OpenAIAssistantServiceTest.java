/*
 * Copyright (c) 2024 Jean Schmitz.
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

package com.talkforgeai.backend;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkforgeai.service.openai.assistant.OpenAIAssistantService;
import com.talkforgeai.service.openai.assistant.dto.Assistant;
import com.talkforgeai.service.openai.exception.OpenAIException;
import com.talkforgeai.service.properties.OpenAIProperties;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = OpenAIAssistantService.class)
public class OpenAIAssistantServiceTest {

  @Inject
  private OpenAIAssistantService openAIAssistantService;

  @MockBean
  private OkHttpClient okHttpClient;

  @MockBean
  private OpenAIProperties openAIProperties;

  @BeforeEach
  public void setUp() {
    when(openAIProperties.apiKey()).thenReturn("api-key");
    when(openAIProperties.apiUrl()).thenReturn("http://api-url");
    when(openAIProperties.chatUrl()).thenReturn("http://char-url");
    when(openAIProperties.imageUrl()).thenReturn("http://image-url");
  }

  @Test
  public void createAssistantReturnsExpectedAssistant() throws IOException {
    // Given
    Assistant expectedAssistant = new Assistant("param1", "param2", "param3", "param4", "param5",
        "param6", "param7", new ArrayList<>(), new ArrayList<>(), null);

    String assistantJson = new ObjectMapper().writeValueAsString(expectedAssistant);

    Response response = mock(Response.class);
    ResponseBody responseBody = ResponseBody.create(assistantJson, OpenAIAssistantService.JSON);
    when(response.body()).thenReturn(responseBody);
    when(response.isSuccessful()).thenReturn(true);

    Call call = mock(Call.class);
    when(call.execute()).thenReturn(response);
    when(okHttpClient.newCall(any(Request.class))).thenReturn(call);

    // When
    openAIAssistantService.createAssistant(expectedAssistant);

    // Then
    verify(okHttpClient).newCall(any(Request.class));
  }

  @Test
  public void createAssistantThrowsExceptionWhenResponseIsUnsuccessful() throws IOException {
    // Given
    Assistant expectedAssistant = new Assistant("param1", "param2", "param3", "param4", "param5",
        "param6", "param7", new ArrayList<>(), new ArrayList<>(), null);

    Response response = mock(Response.class);
    when(response.isSuccessful()).thenReturn(false);

    Call call = mock(Call.class);
    when(call.execute()).thenReturn(response);
    when(okHttpClient.newCall(any(Request.class))).thenReturn(call);

    // Then
    assertThrows(OpenAIException.class,
        () -> openAIAssistantService.createAssistant(expectedAssistant));
  }

  @Test
  public void createAssistantThrowsExceptionWhenResponseBodyIsNull() throws IOException {
    // Given
    Assistant expectedAssistant = new Assistant("param1", "param2", "param3", "param4", "param5",
        "param6", "param7", new ArrayList<>(), new ArrayList<>(), null);

    Response response = mock(Response.class);
    when(response.isSuccessful()).thenReturn(true);
    when(response.body()).thenReturn(null);

    Call call = mock(Call.class);
    when(call.execute()).thenReturn(response);
    when(okHttpClient.newCall(any(Request.class))).thenReturn(call);

    // Then
    assertThrows(OpenAIException.class,
        () -> openAIAssistantService.createAssistant(expectedAssistant));
  }
}
