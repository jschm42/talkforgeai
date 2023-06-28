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
