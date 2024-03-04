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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarkdownHeaderTransformer implements Transformer {

  public static final Logger LOGGER = LoggerFactory.getLogger(MarkdownHeaderTransformer.class);

  private final Pattern regExH2 = Pattern.compile("### (.*)\n");
  private final Pattern regExH1 = Pattern.compile("### (.*)\\n");

  @Override
  public String process(String content, TransformerContext context) {
    content = regExH2.matcher(content).replaceAll("<h2>$1</h2>");
    content = regExH1.matcher(content).replaceAll("<h1>$1</h1>");

    return content;
  }

}
