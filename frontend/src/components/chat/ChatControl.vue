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
  <div class="p-2 m-1 rounded shadow">

    <div class="d-flex flex-row">
      <div class="flex-grow-1">
        <div class="form-check form-switch d-flex switch-panel mt-3">
          <input id="flexCheckDefault" v-model="this.chatStore.isAutoSpeak"
                 class="form-check-input" role="switch" type="checkbox">
          <label class="form-check-label mx-2" for="flexCheckDefault">
            Auto speak
          </label>
        </div>
      </div>
      <button :hidden="isRegenerateRequestHidden" class="btn btn-outline-warning my-2"
              @click="onRegenerateRun()">Regenerate request
      </button>
      <button :hidden="isCancelRequestHidden" class="btn btn-outline-warning my-2"
              @click="onCancelRun()">Cancel request
      </button>
    </div>
    <div class="row">
      <ChatMessageInput @submit-result-received="submitResultReceived"
                        @chunk-update-received="chunkUpdateReceived"></ChatMessageInput>
    </div>
  </div>
</template>

<script>
import {useChatStore} from '@/store/chat-store';
import ChatMessageInput from '@/components/chat/ChatMessageInput.vue';
import {useAppStore} from '@/store/app-store';

export default {
  name: 'ChatControl',
  components: {ChatMessageInput},
  data() {
    return {};
  },
  computed: {
    isCancelRequestHidden() {
      return this.chatStore.runId === '';
    },
    isRegenerateRequestHidden() {
      return !this.appStore.hasErrorState();
    },
  },
  methods: {
    submitResultReceived() {
      console.log('Chat Control - Submit Result Received');
      this.$emit('submitResultReceived');
    },
    chunkUpdateReceived() {
      console.log('Chat Control - Chunk Update Received');
      this.$emit('chunkUpdateReceived');
    },
    onCancelRun() {
      console.log('Chat Control - Cancel Run');
      try {
        this.chatStore.cancelCurrentRun();
      } catch (error) {
        this.appStore.handleError(error);
        this.chatStore.updateStatus('Error: ' + error, 'error');
      }
    },
    async onRegenerateRun() {
      console.log('Chat Control - Regenerate Run');
      this.appStore.resetErrorState();
      try {
        await this.chatStore.regenerateCurrentRun();
      } catch (error) {
        this.appStore.handleError(error);
        this.chatStore.updateStatus('Error: ' + error, 'error');
      }
    },
  },
  setup() {
    const chatStore = useChatStore(); // Call useMyStore() inside the setup function
    const appStore = useAppStore();

    return {chatStore, appStore};
  },
};
</script>

<style scoped>
.switch-panel {
  color: white;
  margin-bottom: 5px;
}
</style>
