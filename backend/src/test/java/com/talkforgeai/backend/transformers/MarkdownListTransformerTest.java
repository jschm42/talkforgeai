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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MarkdownListTransformerTest extends AbstractTransformerTest {

  @Test
  public void testConvertToHtmlListWithLongInput() {
    String inputText = """
        Sure, here's a concise list focusing broadly on general health, suitable for most people, including developers:

        - **Regular Physical Activity:**
          - Engage in at least 30 minutes of moderate to vigorous exercise most days of the week.
          - Incorporate both aerobic exercises (like walking, swimming, or cycling) and muscle-strengthening activities.

        - **Balanced Diet:**
          - Eat a variety of fruits, vegetables, whole grains, and lean proteins.
          - Limit intake of sugars, saturated fats, and sodium.
          
        2. ** Stress Management:**
          - Practice mindfulness, meditation, or yoga.
          - Engage in hobbies or activities that bring joy and relaxation.
                   
        Bla bla bla bla
        """;

    String expectedOutput = """
        Sure, here's a concise list focusing broadly on general health, suitable for most people, including developers:

        <ul><li><strong>Regular Physical Activity:</strong><ul><li>Engage in at least 30 minutes of moderate to vigorous exercise most days of the week.</li><li>Incorporate both aerobic exercises (like walking, swimming, or cycling) and muscle-strengthening activities.</li></ul></li></ul>

        <ul><li><strong>Balanced Diet:</strong><ul><li>Eat a variety of fruits, vegetables, whole grains, and lean proteins.</li><li>Limit intake of sugars, saturated fats, and sodium.</li></ul></li></ul>

        <ul><li><strong>Stress Management:</strong><ul><li>Practice mindfulness, meditation, or yoga.</li><li>Engage in hobbies or activities that bring joy and relaxation.</li></ul></li></ul>

        Bla bla bla bla
        """;

    String actualOutput = new MarkdownListTransformer().process(inputText, context);

    assertEquals(expectedOutput, actualOutput);
  }

  @Test
  public void testConvertToHtmlListWithVariant2() {
    String inputText = """
        Here are some tips to live a healthy life, inspired by the habits of cats:
        1. **Frequent, Short Naps:** To be as alert and agile as a cat, one must master the art of napping.
                
        2. **Hydration is Key:** Always have fresh water within paw's reach.
                
        Bla bla bla     
        """;

    String expectedOutput = """
        Here are some tips to live a healthy life, inspired by the habits of cats:
        <ul><li><strong>Frequent, Short Naps:</strong> To be as alert and agile as a cat, one must master the art of napping.</ul>
                
        <ul><li><strong>Hydration is Key:</strong> Always have fresh water within paw's reach.</ul>
                
        Bla bla bla
        """;

    String actualOutput = new MarkdownListTransformer().process(inputText, context);

    assertEquals(expectedOutput, actualOutput);
  }
}
