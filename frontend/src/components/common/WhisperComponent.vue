<!--
  - Copyright (c) 2023-2024 Jean Schmitz.
  -
  - Licensed under the Apache License, Version 2.0 (the "License");
  - you may not use this file except in compliance with the License.
  - You may obtain a copy of the License at
  -
  -     http://www.apache.org/licenses/LICENSE-2.0
  -
  - Unless required by applicable law or agreed to in writing, software
  - distributed under the License is distributed on an "AS IS" BASIS,
  - WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  - See the License for the specific language governing permissions and
  - limitations under the License.
  -->

<template>
  <button
      :class="{ 'bi-mic-fill': !isRecording, 'bi-record-circle': isRecording }"
      class="bi record-button"
      @mousedown="startRecording($event)"
      @mouseup="stopRecording($event)"
  >
  </button>
</template>

<style scoped>
.record-button {
  font-size: 2em;
}
</style>

<script>
import axios from 'axios';
import {onMounted, ref} from 'vue';
import RecordRTC from 'recordrtc';

export default {
  name: 'WhisperComponent',
  setup(props, {emit}) {
    const recordedBlobUrl = ref(null);
    let isRecording = ref(false);
    let mediaStream = null;
    let recorder = null;

    const startRecording = async () => {
      console.log('Start recording');
      isRecording = true;

      mediaStream = await navigator.mediaDevices.getUserMedia({audio: true});
      recorder = new RecordRTC(mediaStream, {
        type: 'audio',
        mimeType: 'audio/webm',
        sampleRate: 44100,
        numberOfAudioChannels: 1,
        desiredSampRate: 16000,
        bitsPerSecond: 128000,
      });

      await recorder.startRecording();
    };

    const saveBlob = (blob) => {
      console.log('Saving blob: ', blob);
      let formData = new FormData();
      formData.append('file', blob);

      try {
        axios.post('/api/v1/stt/convert', formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        }).then((response) => {
          console.log('Response: ', response);
          const text = response.data;
          emit('onReceiveText', text);
        });
      } catch (e) {
        console.error('Failed to send audio file: ', e);
      } finally {
        isRecording = false;
      }
    };

    const stopRecording = () => {
      recorder.stopRecording(() => {
        const blob = recorder.getBlob();
        saveBlob(blob);
      });
    };

    onMounted(() => {
      if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        console.error('getUserMedia() not supported.');
      }
    });

    return {
      recordedBlobUrl,
      startRecording,
      stopRecording,
      isRecording,
    };
  },
};
</script>
