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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CodeBlockTransformerTest extends AbstractTransformerTest {

  @Test
  public void codeBlockWithLangGetsTransformed() {
    String content = "Here's an example of how to use the \"Arrays.sort()\" method:\n" +
        "\n" +
        "```java\n" +
        "int[] numbers = { 5, 3, 9, 1, 7 };\n" +
        "Arrays.sort(numbers);\n" +
        "```\n" +
        "\n" +
        "<smiley>";

    String expected = """
        Here's an example of how to use the "Arrays.sort()" method:
                        
        <!-- start no-lb -->
        <pre>
          <code class="language-java"
        >int[] numbers = { 5, 3, 9, 1, 7 };
        Arrays.sort(numbers);</code>
        </pre>
        <!-- end no-lb -->
                        
                        
        <smiley>""";

    CodeBlockTransformer transformer = new CodeBlockTransformer();

    String processed = transformer.process(content, context);

    Assertions.assertEquals(expected, processed);

  }

  @Test
  public void codeBlockWithoutLangGetsTransformed() {
    String content = "Here's an example of how to use the \"Arrays.sort()\" method:\n" +
        "\n" +
        "```\n" +
        "int[] numbers = { 5, 3, 9, 1, 7 };\n" +
        "Arrays.sort(numbers);\n" +
        "```\n" +
        "\n" +
        "<smiley>";

    String expected = """
        Here's an example of how to use the "Arrays.sort()" method:
                                
        <!-- start no-lb -->
        <pre>
          <code >int[] numbers = { 5, 3, 9, 1, 7 };
        Arrays.sort(numbers);</code>
        </pre>
        <!-- end no-lb -->
                                
                                
        <smiley>""";

    CodeBlockTransformer transformer = new CodeBlockTransformer();

    String processed = transformer.process(content, context);

    Assertions.assertEquals(expected, processed);

  }

}
