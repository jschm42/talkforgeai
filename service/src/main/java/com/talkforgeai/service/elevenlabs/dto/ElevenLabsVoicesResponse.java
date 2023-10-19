package com.talkforgeai.service.elevenlabs.dto;

import java.util.List;

public class ElevenLabsVoicesResponse {
    private List<ElevenLabsVoice> voices;

    public List<ElevenLabsVoice> getVoices() {
        return voices;
    }

    public void setVoices(List<ElevenLabsVoice> voices) {
        this.voices = voices;
    }
}
