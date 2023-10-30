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

public class NewLineTransformer implements Transformer {

    @Override
    public String process(String content, TransformerContext context) {
        return replaceOutsideCodeBlock(content);
    }

    private String replaceOutsideCodeBlock(String str) {
        String[] parts = str.split("(?=" + NO_LB_MARKER_START + ")|(?<=" + NO_LB_MARKER_END + ")");

        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            if (!part.startsWith(NO_LB_MARKER_START)) {
                part = part
                        .replaceAll("\n\n", "<p/>")
                        .replaceAll("\n", "<br/>");
            }
            result.append(part);
        }

        return result.toString();
    }
}
