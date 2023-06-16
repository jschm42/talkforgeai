package com.talkforgeai.talkforgeaiserver.openai.dto;

import java.util.Date;
import java.util.List;


public class OpenAIResponse {
    private String id;
    private Date created;
    private String model;

    private OpenAIResponseUsage usage;

    private List<OpenAIChatCompletionChoice> choices;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public OpenAIResponseUsage getUsage() {
        return usage;
    }

    public void setUsage(OpenAIResponseUsage usage) {
        this.usage = usage;
    }

    public List<OpenAIChatCompletionChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<OpenAIChatCompletionChoice> choices) {
        this.choices = choices;
    }
}
