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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarkdownHeaderTransformer implements Transformer {

  public static final Logger LOGGER = LoggerFactory.getLogger(MarkdownHeaderTransformer.class);

  @Override
  public String process(String content, TransformerContext context) {
    /**
     * Transforms markdown code blocks into HTML code blocks.
     *
     * Before:
     * ### Key Points:
     *
     * After:
     * <h3>Key Points:</h3>
     */

    String regexH2 = "#### (.*)\\n";
    String regexH3 = "### (.*)\\n";

    content = content.replaceAll(regexH2, "<h2>$1</h2>");
    content = content.replaceAll(regexH3, "<h3>$1</h3>");

    return content;
  }

}
