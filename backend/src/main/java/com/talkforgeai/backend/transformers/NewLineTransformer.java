package com.talkforgeai.backend.transformers;

import com.talkforgeai.backend.transformers.dto.TransformerContext;

public class NewLineTransformer implements Transformer {

    @Override
    public String process(String content, TransformerContext context) {
        return replaceOutsideCodeBlock(content);
    }

    private String replaceOutsideCodeBlock(String str) {
        String[] parts = str.split("(?=<pre><code)|(?<=</code></pre>)");
        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            if (!part.startsWith("<pre")) {
                part = part
                        .replaceAll("\n\n", "<p/>")
                        .replaceAll("\n", "<br/>");
            }
            result.append(part);
        }

        return result.toString();
    }
}
