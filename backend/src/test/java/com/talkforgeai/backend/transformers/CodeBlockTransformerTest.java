package com.talkforgeai.backend.transformers;

import com.talkforgeai.backend.transformers.dto.TransformerContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.UUID;

public class CodeBlockTransformerTest {
    TransformerContext context;

    @BeforeEach
    public void before() {
        context = new TransformerContext(UUID.randomUUID(),
                Path.of("/temp/persona"),
                Path.of("/temp/persona/import"),
                Path.of("/temp/chat")
        );
    }

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
