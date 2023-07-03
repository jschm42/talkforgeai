package com.talkforgeai.backend.transformers;

import com.talkforgeai.backend.transformers.dto.TransformerContext;

public interface Transformer {
    String process(String content, TransformerContext context);
}
