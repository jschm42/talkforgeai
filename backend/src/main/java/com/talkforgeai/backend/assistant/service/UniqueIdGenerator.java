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

package com.talkforgeai.backend.assistant.service;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class UniqueIdGenerator {

  private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final SecureRandom random = new SecureRandom();

  public String generateThreadId() {
    return generateUniqueId("thread", 20);
  }

  public String generateMessageId() {
    return generateUniqueId("msg", 20);
  }

  public String generateAssistantId() {
    return generateUniqueId("asst", 20);
  }

  public String generateUniqueId(String prefix, int length) {
    String randomChars = generateRandomString(length);
    return prefix + "_" + randomChars;
  }

  private String generateRandomString(int length) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
    }
    return sb.toString();
  }
}
