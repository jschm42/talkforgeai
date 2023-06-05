package com.talkforgeai.talkforgeaiserver.transformers;

import com.talkforgeai.talkforgeaiserver.transformers.dto.TransformerContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.UUID;

public class CodeBlockTransformerTest {
    TransformerContext context;

    @BeforeEach
    public void before() {
        context = new TransformerContext(UUID.randomUUID(), Path.of("/temp"));
    }

//     "<smiley> \n" +
//             "\n" +
//             "Here's an example of how to use the \"Arrays.sort()\" method to sort an array of integers in ascending order:\n" +
//             "\n" +
//             "```java\n" +
//             "int[] numbers = { 5, 3, 9, 1, 7 };\n" +
//             "Arrays.sort(numbers);\n" +
//             "```\n" +
//             "\n" +
//                "This will sort the \"numbers\" array in ascending order, so the array will now contain {1, 3, 5, 7, 9}.\n" +
//                "\n" +
//                "To sort in descending order, we need to use a custom comparator method as follows.\n" +
//                "\n" +
//                "```java\n" +
//                "int[] numbers = { 5, 3, 9, 1, 7 };\n" +
//                "Arrays.sort(numbers, new Comparator<Integer>() {\n" +
//                "    @Override\n" +
//                "    public int compare(Integer o1, Integer o2) {\n" +
//                "        return o2.compareTo(o1);\n" +
//                "    }\n" +
//                "});\n" +
//                "```\n" +
//                "\n" +
//                "This will sort the \"numbers\" array in descending order, so the array will now contain {9, 7, 5, 3, 1}.\n" +
//                "\n" +
//                "Once the array has been sorted, you can access the elements in their new order using a loop or calling specific indexes.\n" +
//                "\n" +
//             "<smiley>"

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

        String expected = "Here's an example of how to use the \"Arrays.sort()\" method:\n" +
                "\n" +
                "<pre><code class=\"language-java\">" +
                "int[] numbers = { 5, 3, 9, 1, 7 };\n" +
                "Arrays.sort(numbers);\n" +
                "</code></pre>\n" +
                "\n" +
                "<smiley>";

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

        String expected = "Here's an example of how to use the \"Arrays.sort()\" method:\n" +
                "\n" +
                "<pre><code >" +
                "int[] numbers = { 5, 3, 9, 1, 7 };\n" +
                "Arrays.sort(numbers);\n" +
                "</code></pre>\n" +
                "\n" +
                "<smiley>";

        CodeBlockTransformer transformer = new CodeBlockTransformer();

        String processed = transformer.process(content, context);

        Assertions.assertEquals(expected, processed);

    }
}
