package com.talkforgeai.talkforgeaiserver.transformers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeBlockTransformer implements Transformer {

    @Override
    public String process(String content) {
        String regex = "```([a-z]*)\n([\\s\\S]*?)```";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String lang = matcher.group(1);
            String code = matcher.group(2);

            String processed = """
                    <pre><code class="language-%s">%s</code></pre>
                    """.formatted(lang, code);

            content = content.replace(matcher.group(), processed);
            System.out.println("Replaced: " + content);
        }

        return content;
    }
}
