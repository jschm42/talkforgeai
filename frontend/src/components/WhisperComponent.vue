<template>
  <div>
    <button @mousedown="startRecording" @mouseup="stopRecording">Hold to Record</button>
    <p>{{ text }}</p>
  </div>
</template>

<script>
import axios from 'axios';
import RecordRTC from 'recordrtc';

export default {
  data() {
    return {
      text: '',
      recorder: null,
    };
  },
  methods: {
    async startRecording() {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({audio: true});
        this.recorder = RecordRTC(stream, {type: 'audio'});
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

          this.text = response.data;
        } catch (e) {
          console.error('Failed to send audio file: ', e);
        }
      });
    },
  },
};
</script>
