<template>
  <div class="p-2 m-1 rounded shadow input-container">

    <div class="row">
      <div class="col-12">
        <div class="form-check form-switch d-flex switch-panel">
          <input id="flexCheckDefault" v-model="localIsAutoSpeak"
                 class="form-check-input" role="switch" type="checkbox">
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
import {ref, watch} from 'vue';

export default {
  name: 'ChatControl',
  components: {ChatMessageInput},
  data() {
    return {
      isAutoSpeak: Boolean,
    };
  },
  getters: {
    isAutoSpeak() {
      return this.store.chat.autoSpeak;
    },
  },
  methods: {
    submitResultReceived() {
      this.$emit('submitResultReceived');
    },
    chunkUpdateReceived() {
      this.$emit('chunkUpdateReceived');
    },
  },
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function
    const localIsAutoSpeak = ref(store.chat.autoSpeak);

    watch(() => store.chat.autoSpeak, (newValue) => {
      localIsAutoSpeak.value = newValue;
    });

    return {store, localIsAutoSpeak};
  },
};
</script>

<style scoped>
.switch-panel {
  color: white;
  margin-bottom: 5px;
}
</style>
