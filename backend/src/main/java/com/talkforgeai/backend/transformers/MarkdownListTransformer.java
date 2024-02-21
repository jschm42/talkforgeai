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

public class MarkdownListTransformer implements Transformer {

  public static final Logger LOGGER = LoggerFactory.getLogger(MarkdownListTransformer.class);

  @Override
  public String process(String content, TransformerContext context) {
    /**
     * Transforms markdown code blocks into HTML code blocks.
     *
     * Before:
     * \n\n- **Physical Health:**\n
     * - **Regular Exercise:** Aim for at least 30 minutes of moderate exercise most days of the week. This can include walking, cycling, or even short breaks for stretching or yoga during your coding sessions.\n
     * - **Healthy Eating:** Focus on a balanced diet rich in fruits, vegetables, whole grains, and lean proteins. Remember to hydrate well.\n
     * - **Adequate Sleep:** Ensure 7-9 hours of quality sleep each night to help your body and mind recover and rejuvenate.\n\n
     *
     * After:
     * <li><strong>Physical Health:</strong></li>
     * <ul><strong>Regular Exercise:</strong> Aim for at least 30 minutes of moderate exercise most days of the week. This can include walking, cycling, or even short breaks for stretching or yoga during your coding sessions.</ul>
     * <ul><strong>Healthy Eating:</strong> Focus on a balanced diet rich in fruits, vegetables, whole grains, and lean proteins. Remember to hydrate well.</ul>
     * <ul><strong>Adequate Sleep:</strong> Ensure 7-9 hours of quality sleep each night to help your body and mind recover and rejuvenate.</ul>
     * </li>
     */

    return content;
  }


}
