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

package com.talkforgeai.backend.transformers;

import com.talkforgeai.backend.transformers.dto.TransformerContext;
import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.EntityArrays;
import org.apache.commons.text.translate.LookupTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlEncoderTransformer implements Transformer {

  public static final Logger LOGGER = LoggerFactory.getLogger(HtmlEncoderTransformer.class);

  @Override
  public String process(String content, TransformerContext context) {
    CharSequenceTranslator escapeHtml4 = new AggregateTranslator(
        new LookupTranslator(EntityArrays.BASIC_ESCAPE),
        new LookupTranslator(EntityArrays.ISO8859_1_ESCAPE),
        new LookupTranslator(EntityArrays.HTML40_EXTENDED_ESCAPE)
    );

    return escapeHtml4.translate(content);
  }
}
