<template>
  <i :class="{'bi-mic-fill': !isRecording, 'bi-record-circle': isRecording}" class="bi record-button" role="button"
     @mousedown="startRecording"
     @mouseup="stopRecording"></i>
</template>

<style scoped>
.record-button {
  font-size: 2em;
}
</style>

<script>
import axios from 'axios';
import RecordRTC from 'recordrtc';

export default {
  name: 'WhisperComponent',
  data() {
    return {
      text: '',
      recorder: null,
      isRecording: false,
    };
  },
  mounted() {
    window.addEventListener('keydown', this.handleKeydown);
  },
  beforeUnmount() {
    window.removeEventListener('keydown', this.handleKeydown);
  },
  methods: {
    async handleKeydown(event) {
      if (event.ctrlKey && event.key === 'r') {
        event.preventDefault(); // Prevent default behavior
        if (this.isRecording) {
          await this.stopRecording();
        } else {
          await this.startRecording();
        }
      }
    },
    async startRecording() {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({audio: true});
        this.recorder = RecordRTC(stream, {type: 'audio'});
        this.isRecording = true;
        await this.recorder.startRecording();
      } catch (e) {
        console.error('Failed to start recording: ', e);
      }
    },
    async stopRecording() {
      if (!this.recorder) {
        return;
      }

      await this.recorder.stopRecording(async () => {
        let blob = this.recorder.getBlob();
        let formData = new FormData();
        formData.append('file', blob);

        try {
          const response = await axios.post('http://localhost:8080/api/convert', formData, {
            headers: {
              'Content-Type': 'multipart/form-data',
            },
          });

          this.text = response.data.text;
          this.$emit('onReceiveText', this.text);
        } catch (e) {
          console.error('Failed to send audio file: ', e);
        } finally {
          this.isRecording = false;
        }
      });
    },
  },
};
</script>
