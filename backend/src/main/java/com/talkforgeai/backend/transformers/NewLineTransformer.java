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
