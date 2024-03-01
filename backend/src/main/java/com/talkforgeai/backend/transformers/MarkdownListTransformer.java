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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarkdownListTransformer implements Transformer {

  public static final Logger LOGGER = LoggerFactory.getLogger(MarkdownListTransformer.class);
  final Pattern headerRegEx = Pattern.compile("^(.*) \\*\\*(.*):\\*\\*(.*?)$");

  public String convertToHtmlList(String text) {
    StringBuilder htmlBuilder = new StringBuilder();
    String[] lines = text.split("\n");
    boolean isListOpen = false;
    boolean isSubList = false;

    for (String line : lines) {
      // Check if the line is a list header
      // A list header is a line that starts with some text, followed by "**" e.g. "2. **", or "- **"
      Matcher matcher = headerRegEx.matcher(line.trim());

      if (matcher.find()) {
        String headerPrefix = matcher.group(1);
        String headerTitle = matcher.group(2).trim();
        String headerContent = matcher.group(3);

        if (!isListOpen) {
          htmlBuilder.append("<ul>");
          isListOpen = true;
        } else if (isSubList) {
          htmlBuilder.append("</ul></li>"); // Close previous sublist
          isSubList = false;
        }
        //String header = line.substring(line.indexOf("**") + 2, line.lastIndexOf("**")).trim();
        htmlBuilder.append("<li>").append("<strong>").append(headerTitle)
            .append(":</strong>").append(headerContent);
      }
      // Check if the line is a list item
      else if (line.trim().startsWith("- ")) {
        if (!isSubList && isListOpen) {
          htmlBuilder.append("<ul>");
          isSubList = true;
        }
        String item = line.trim().substring(2);
        htmlBuilder.append("<li>").append(item).append("</li>");
      }
      // Handle non-list text
      else {
        if (isSubList) {
          htmlBuilder.append("</ul></li>");
          isSubList = false;
        }
        if (isListOpen) {
          htmlBuilder.append("</ul>\n");
          isListOpen = false;
        }
        htmlBuilder.append(line).append("\n");
      }
    }
    // Close any open tags at the end of the input
    if (isSubList) {
      htmlBuilder.append("</ul></li>");
    }
    if (isListOpen) {
      htmlBuilder.append("</ul>");
    }

    return htmlBuilder.toString();
  }

  @Override
  public String process(String content, TransformerContext context) {
    return convertToHtmlList(content);
  }


}
