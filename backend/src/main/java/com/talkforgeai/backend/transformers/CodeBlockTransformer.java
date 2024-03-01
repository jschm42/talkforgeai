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

public class CodeBlockTransformer implements Transformer {

  public static final Logger LOGGER = LoggerFactory.getLogger(CodeBlockTransformer.class);

  @Override
  public String process(String content, TransformerContext context) {
    String regex = "```(.*)\n([\\s\\S]*?)```";

    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(content);

    while (matcher.find()) {
      String lang = matcher.group(1);
      String processed = getProcessedContent(matcher, lang);

      content = content.replace(matcher.group(), processed);
      LOGGER.debug("Replaced: {}", content);
    }

    return content;
  }

  private String getProcessedContent(Matcher matcher, String lang) {
    String code = matcher.group(2);

    String langClass = "";
    if (lang != null && !lang.isEmpty()) {
      langClass = """
          class="language-%s"
          """.formatted(lang);
    }

    return """
        %s
        <pre>
          <code %s>%s</code>
        </pre>
        %s
        """.formatted(NO_LB_MARKER_START, langClass, code, NO_LB_MARKER_END);
  }
}
