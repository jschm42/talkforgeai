<template>
  <WhisperComponent></WhisperComponent>

  <RadioPlayer></RadioPlayer>
</template>

<script>
import WhisperComponent from '@/components/WhisperComponent.vue';
import RadioPlayer from '@/components/RadioPlayer.vue';

export default {
  name: 'SessionSystem',
  components: {WhisperComponent, RadioPlayer},
};
</script>

<style scoped>

</style>
