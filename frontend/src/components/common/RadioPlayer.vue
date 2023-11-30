<!--
  - Copyright (c) 2023 Jean Schmitz.
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
  <div>
    <button @click="playAudio">Play</button>
    <button @click="stopAudio">Stop</button>
    <p v-if="isPlaying">Playing...</p>
    <p v-else>Stopped</p>
  </div>
</template>

<script>
import {ref} from 'vue';

export default {
  setup() {
    const audio = ref(null);
    const isPlaying = ref(false);

    const playAudio = () => {
      audio.value = new Audio('http://localhost:8090/streamaudio'); // replace with your streaming endpoint
      audio.value.play();
      isPlaying.value = true;
      audio.value.onended = () => isPlaying.value = false;
    };

    const stopAudio = () => {
      if (audio.value) {
        audio.value.pause();
        audio.value = null;
      }
      isPlaying.value = false;
    };

    return {
      playAudio,
      stopAudio,
      isPlaying,
    };
  },
};
</script>

<style scoped>

</style>
