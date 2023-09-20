package com.talkforgeai.backend.transformers;

import com.talkforgeai.backend.transformers.dto.TransformerContext;

public interface Transformer {
    String NO_LB_MARKER_START = "<!-- start no-lb -->";
    String NO_LB_MARKER_END = "<!-- end no-lb -->";

    String process(String content, TransformerContext context);
}
