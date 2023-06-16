package com.talkforgeai.talkforgeaiserver.openai.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenAIChatCompletionChoice {
    Integer index;
    @JsonAlias({"delta"})
    OpenAIChatMessage message;
    @JsonProperty("finish_reason")
    String finishReason;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }


    public String getFinishReason() {
        return finishReason;
    }

    public void setFinishReason(String finishReason) {
        this.finishReason = finishReason;
    }

    public OpenAIChatMessage getMessage() {
        return message;
    }

    public void setMessage(OpenAIChatMessage message) {
        this.message = message;
    }
}
