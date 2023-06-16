package com.talkforgeai.talkforgeaiserver.openai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenAIRequest {
    String model;
    List<OpenAIChatMessage> messages;

    List<OpenAIFunction> functions;

    Double temperature;
    @JsonProperty("top_p")
    Double topP;
    Integer n;
    Boolean stream;
    List<String> stop;
    @JsonProperty("max_tokens")
    Integer maxTokens;
    @JsonProperty("presence_penalty")
    Double presencePenalty;
    @JsonProperty("frequency_penalty")
    Double frequencyPenalty;
    @JsonProperty("logit_bias")
    Map<String, Integer> logitBias;
    String user;

    public OpenAIRequest() {
        this.functions = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getTopP() {
        return topP;
    }

    public void setTopP(Double topP) {
        this.topP = topP;
    }

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public Boolean getStream() {
        return stream;
    }

    public void setStream(Boolean stream) {
        this.stream = stream;
    }

    public List<String> getStop() {
        return stop;
    }

    public void setStop(List<String> stop) {
        this.stop = stop;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Double getPresencePenalty() {
        return presencePenalty;
    }

    public void setPresencePenalty(Double presencePenalty) {
        this.presencePenalty = presencePenalty;
    }

    public Double getFrequencyPenalty() {
        return frequencyPenalty;
    }

    public void setFrequencyPenalty(Double frequencyPenalty) {
        this.frequencyPenalty = frequencyPenalty;
    }

    public Map<String, Integer> getLogitBias() {
        return logitBias;
    }

    public void setLogitBias(Map<String, Integer> logitBias) {
        this.logitBias = logitBias;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<OpenAIChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<OpenAIChatMessage> messages) {
        this.messages = messages;
    }

    public List<OpenAIFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<OpenAIFunction> functions) {
        this.functions = functions;
    }
}
