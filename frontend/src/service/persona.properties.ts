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

const PersonaProperties = {
  TTS_TYPE: 'tts_type',
  SPEECHAPI_VOICE: 'speechAPI_voice',
  ELEVENLABS_VOICEID: 'elevenlabs_voiceId',
  ELEVENLABS_MODELID: 'elevenlabs_modelId',
  ELEVENLABS_SIMILARITYBOOST: 'elevenlabs_similarityBoost',
  ELEVENLABS_STABILITY: 'elevenlabs_stability',
  CHATGPT_MODEL: 'chatgpt_model',
  CHATGPT_TEMPERATURE: 'chatgpt_temperature',
  CHATGPT_TOP_P: 'chatgpt_topP',
  CHATGPT_FREQUENCY_PENALTY: 'chatgpt_frequencyPenalty',
  CHATGPT_PRESENCE_PENALTY: 'chatgpt_presencePenalty',
  FEATURE_PLANTUML: 'feature_plantUMLGeneration',
  FEATURE_IMAGEGENERATION: 'feature_imageGeneration',
  FEATURE_AUTOSPEAKDEFAULT: 'feature_autoSpeakDefault',
  FEATURE_TITLEGENERATION: 'feature_titleGeneration',
};

const TTSType = {
  ELEVENLABS: 'elevenlabs',
  SPEECHAPI: 'speechAPI',
  DISABLED: '',
};

export default PersonaProperties;
export {TTSType};

