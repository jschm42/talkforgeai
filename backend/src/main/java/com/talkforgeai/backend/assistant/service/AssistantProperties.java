/*
 * Copyright (c) 2023 Jean Schmitz.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.talkforgeai.backend.assistant.service;

public enum AssistantProperties {
    TTS_TYPE("tts_type", ""),

    SPEECHAPI_VOICE("speechAPI_voice", ""),

    ELEVENLABS_VOICEID("elevenlabs_voiceId", ""),
    ELEVENLABS_MODELID("elevenlabs_modelId", "eleven_monolingual_v2"),
    ELEVENLABS_SIMILARITYBOOST("elevenlabs_similarityBoost", "0"),
    ELEVENLABS_STABILITY("elevenlabs_stability", "0"),

    CHATGPT_MODEL("chatgpt_model", "gpt-4"),
    CHATGPT_TEMPERATURE("chatgpt_temperature", "0.7"),
    CHATGPT_TOP_P("chatgpt_topP", "1.0"),
    CHATGPT_FREQUENCY_PENALTY("chatgpt_frequencyPenalty", "0"),
    CHATGPT_PRESENCE_PENALTY("chatgpt_presencePenalty", "0"),
    FEATURE_PLANTUML("feature_plantUMLGeneration", "false"),
    FEATURE_IMAGEGENERATION("feature_imageGeneration", "false"),
    FEATURE_AUTOSPEAKDEFAULT("feature_autoSpeakDefault", "false"),
    FEATURE_TITLEGENERATION("feature_titleGeneration", "true");
    private final String key;
    private final String defaultValue;

    AssistantProperties(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getKey() {
        return key;
    }
}
