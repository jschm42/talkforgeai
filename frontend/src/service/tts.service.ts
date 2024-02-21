/*
 * Copyright (c) 2023-2024 Jean Schmitz.
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

import axios from 'axios';
import AssistantProperties from '@/service/assistant.properties';
import Assistant from '@/store/to/assistant';

class TtsService {

  async getElevenlabsVoices() {
    try {
      const result = await axios.get(
          `/api/v1/tts/voices`,
          {
            timeout: 5000,
            headers: {
              'Content-Type': 'application/json',
            },
          });
      return result.data;
    } catch (error) {
      console.error('Error submitting prompt: ', error);
    }
  }

  async getElevenlabsModels() {
    try {
      const result = await axios.get(
          `/api/v1/tts/models`,
          {
            timeout: 5000,
            headers: {
              'Content-Type': 'application/json',
            },
          });
      return result.data;
    } catch (error) {
      console.error('Error submitting prompt: ', error);
    }
  }

  async speakElevenlabs(text: string, assistantId: string) {
    try {
      const result = await axios.post(
          `/api/v1/tts/stream`,
          {text, assistantId},
          {
            timeout: 50000,
            headers: {
              'Content-Type': 'application/json',
            },
            responseType: 'blob',
          });
      return result.data;
    } catch (error) {
      console.error('Error submitting prompt: ', error);
    }
    return null;
  }

  async speakSpeechAPI(text: string, assistant: Assistant) {
    return new Promise((resolve) => {
      const msg = new SpeechSynthesisUtterance();
      msg.text = text;
      const personaVoice = assistant.properties[AssistantProperties.SPEECHAPI_VOICE];
      const voice = window.speechSynthesis.getVoices().find((voice) => voice.name === personaVoice);

      if (voice) {
        console.log('Using voice: ', voice);
        msg.voice = voice;
      }

      msg.onend = function(e) {
        resolve(true);
      };

      window.speechSynthesis.speak(msg);
    });
  }
}

export default TtsService;
