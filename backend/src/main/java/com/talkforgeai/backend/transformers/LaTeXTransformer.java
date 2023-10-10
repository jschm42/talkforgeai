package com.talkforgeai.backend.transformers;

import com.talkforgeai.backend.transformers.dto.TransformerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LaTeXTransformer implements Transformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(LaTeXTransformer.class);
    private static final Pattern UrlRegEx = Pattern.compile("```latex\n([\\s\\S]*?)```", Pattern.MULTILINE);

    final String template = """
            %s
            <tf-latex latex='%s'></tf-latex>
            %s
            """;

    @Override
    public String process(String content, TransformerContext context) {
        Matcher matcher = UrlRegEx.matcher(content);

        while (matcher.find()) {
            String fullTag = matcher.group(0);
            String code = matcher.group(1);

            // Perform your Mustache template replacement here
            String formattedContent = template.formatted(NO_LB_MARKER_START, code, NO_LB_MARKER_END);
            content = content.replace(fullTag, formattedContent);
        }

        return content;
    }

}
