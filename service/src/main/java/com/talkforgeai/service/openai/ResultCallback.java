package com.talkforgeai.service.openai;

import com.talkforgeai.service.openai.dto.OpenAIChatMessage;

@FunctionalInterface
public interface ResultCallback {
    void call(OpenAIChatMessage message);
}
