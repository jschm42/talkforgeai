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

package com.talkforgeai.backend.transformers;

import com.talkforgeai.backend.transformers.dto.TransformerContext;
import java.util.regex.Pattern;

public class NewLineTransformer implements Transformer {

  private final Pattern regExTwoNl = Pattern.compile("\n\n");
  private final Pattern regExNl = Pattern.compile("\n");

  @Override
  public String process(String content, TransformerContext context) {
    return replaceOutsideCodeBlock(content);
  }

  private String replaceOutsideCodeBlock(String str) {
    String[] parts = str.split("(?=" + NO_LB_MARKER_START + ")|(?<=" + NO_LB_MARKER_END + ")");

    for (int i = 0; i < parts.length; i++) {
      if (!parts[i].startsWith(NO_LB_MARKER_START)) {
        parts[i] = regExTwoNl.matcher(parts[i]).replaceAll("<p/>");
        parts[i] = regExNl.matcher(parts[i]).replaceAll("<br/>");
      }
    }

    return String.join("", parts);
  }
}
