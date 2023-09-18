package com.talkforgeai.backend.transformers.dto;

import com.talkforgeai.backend.transformers.Transformer;

public class HTMLEncoderTransformer implements Transformer {

    @Override
    public String process(String content, TransformerContext context) {
        return replaceOutsideCodeBlock(content);
    }

    private String replaceOutsideCodeBlock(String str) {
        String[] parts = str.split("(?=<code)|(?<=</code>)");
        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            if (!part.startsWith("<code")) {
                part = part.replaceAll("&", "&amp;");
                part = part.replaceAll("<", "&lt;");
                part = part.replaceAll(">", "&gt;");
                part = part.replaceAll("\"", "&quot;");
                part = part.replaceAll("'", "&#039;");
            }
            result.append(part);
        }

        return result.toString();
    }
}
