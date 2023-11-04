/*
 * Copyright (c) 2023 Jean Schmitz.
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
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LaTeXTransformer implements Transformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(LaTeXTransformer.class);
    private static final Pattern UrlRegEx = Pattern.compile("```latex\n([\\s\\S]*?)```", Pattern.MULTILINE);

    final String template = """
            %s
            <tf-latex latex='%s'></tf-latex>
            %s
            """;

    @Override
    public String process(String content, TransformerContext context) {
        Matcher matcher = UrlRegEx.matcher(content);

        while (matcher.find()) {
            String fullTag = matcher.group(0);
            String code = matcher.group(1);

            // Perform your Mustache template replacement here
            String formattedContent = template.formatted(NO_LB_MARKER_START, code, NO_LB_MARKER_END);
            content = content.replace(fullTag, formattedContent);
        }

        return content;
    }

}
