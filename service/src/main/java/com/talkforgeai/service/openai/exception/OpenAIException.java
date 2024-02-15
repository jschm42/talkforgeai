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

package com.talkforgeai.service.openai.exception;

import com.talkforgeai.service.openai.assistant.dto.ApiError.ApiErrorBody;

public class OpenAIException extends RuntimeException {

  private ApiErrorBody errorDetail;
  private String detail;

  @Deprecated
  public OpenAIException(String message, String detail) {
    super(message);
    this.detail = detail;
  }

  public OpenAIException(String message, ApiErrorBody errorDetail) {
    super(message);
    this.errorDetail = errorDetail;
  }

  public OpenAIException(String message, Throwable cause) {
    super(message, cause);
  }

  public OpenAIException(String message) {
    super(message);
  }

  @Deprecated
  public String getDetail() {
    return detail;
  }

  public ApiErrorBody getErrorDetail() {
    return errorDetail;
  }
}
