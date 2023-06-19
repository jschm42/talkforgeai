<template>
  <div class="p-2 m-1 rounded shadow input-container">
    <!-- Tabs -->
    <ul id="pills-tab" class="nav nav-pills mb-3" role="tablist">

      <li class="nav-item" role="presentation">
        <button id="pills-message-tab" aria-controls="pills-message" aria-selected="true"
                class="nav-link active"
                data-bs-target="#pills-message" data-bs-toggle="pill" role="tab" type="button">ChatMessage
        </button>
      </li>

      <li class="nav-item" role="presentation">
        <button id="pills-system-tab" aria-controls="pills-system" aria-selected="true"
                class="nav-link"
                data-bs-target="#pills-system" data-bs-toggle="pill" role="tab" type="button">System
        </button>
      </li>

      <li class="nav-item" role="presentation">
        <button id="pills-parameters-tab" aria-controls="pills-parameters" aria-selected="false"
                class="nav-link"
                data-bs-target="#pills-parameters" data-bs-toggle="pill" role="tab" type="button">Parameters
        </button>
      </li>
    </ul>
    <div id="pills-tabContent" class="tab-content">
      <div id="pills-message" aria-labelledby="pills-message-tab" class="tab-pane fade show active"
           role="tabpanel" tabindex="0">
        <div class="form-check">
          <input id="flexCheckDefault" class="form-check-input" type="checkbox" @change="toggleAutoSpeak">
          <label class="form-check-label" for="flexCheckDefault">
            Auto speak
          </label>
        </div>
        <ChatMessageInput @submit-submitResult-received="submitResultReceived"></ChatMessageInput>
      </div>

      <div id="pills-system" aria-labelledby="pills-system-tab" class="tab-pane fade" role="tabpanel"
           tabindex="0">
        <SessionSystem></SessionSystem>
      </div>
      <div id="pills-parameters" aria-labelledby="pills-parameters-tab" class="tab-pane fade" role="tabpanel"
           tabindex="0">
        <SessionParameters></SessionParameters>
      </div>
    </div>
    <!-- Ende Tabs -->
  </div>
</template>

<script>
import {useChatStore} from '@/store/chat-store';
import ChatMessageInput from '@/components/ChatMessageInput.vue';
import SessionSystem from '@/components/SessionSystem.vue';
import SessionParameters from '@/components/SessionParameters.vue';

export default {
  name: 'ChatControl',
  components: {SessionParameters, SessionSystem, ChatMessageInput},
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
  },
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function
    return {store};
  },
};
</script>

<style scoped>

</style>
