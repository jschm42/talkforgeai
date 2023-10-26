<template>
  <div class="p-2 m-1 rounded shadow input-container">

    <div class="row">
      <div class="col-12">
        <div class="form-check form-switch d-flex">
          <input id="flexCheckDefault" class="form-check-input" role="switch" type="checkbox"
                 @change="toggleAutoSpeak">
          <label class="form-check-label mx-2" for="flexCheckDefault">
            Auto speak
          </label>
        </div>
      </div>
    </div>
    <div class="row">
      <ChatMessageInput @submit-result-received="submitResultReceived"
                        @chunk-update-received="chunkUpdateReceived"></ChatMessageInput>
    </div>
  </div>
</template>

<script>
import {useChatStore} from '@/store/chat-store';
import ChatMessageInput from '@/components/ChatMessageInput.vue';

export default {
  name: 'ChatControl',
  components: {ChatMessageInput},
  data() {
    return {};
  },
  methods: {
    toggleAutoSpeak() {
      this.store.toggleAutoSpeak();
    },
    submitResultReceived() {
      this.$emit('submitResultReceived');
    },
    chunkUpdateReceived() {
      this.$emit('chunkUpdateReceived');
    },
  },
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function
    return {store};
  },
};
</script>

<style scoped>
</style>
