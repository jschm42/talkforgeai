package com.talkforgeai.talkforgeaiserver.transformers;

import com.talkforgeai.talkforgeaiserver.transformers.dto.TransformerContext;

public interface Transformer {
    String process(String content, TransformerContext context);
}
