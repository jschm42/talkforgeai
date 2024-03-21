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
  <v-card>
    <v-card-actions>
      <v-checkbox v-model="this.chatStore.isAutoSpeak" label="Auto speak"></v-checkbox>
      <v-btn :hidden="isRegenerateRequestHidden"
             @click="onRegenerateRun()">Regenerate request
      </v-btn>
      <v-btn :hidden="isCancelRequestHidden" @click="onCancelRun()">Cancel request</v-btn>
    </v-card-actions>
    <v-row>
      <v-col>
        <ChatMessageInput @submit-result-received="submitResultReceived"
                          @chunk-update-received="chunkUpdateReceived"></ChatMessageInput>
      </v-col>
    </v-row>
  </v-card>
</template>

<script>
import {useChatStore} from '@/store/chat-store';
import ChatMessageInput from '@/components/chat/ChatMessageInput.vue';
import {useAppStore} from '@/store/app-store';
import {useAssistants} from '@/composable/use-assistants';

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
        this.assistants.cancelCurrentRun();
      } catch (error) {
        this.appStore.handleError(error);
        this.chatStore.updateStatus('Error: ' + error, 'error');
      }
    },
    async onRegenerateRun() {
      console.log('Chat Control - Regenerate Run');
      this.appStore.resetErrorState();
      try {
        await this.assistants.regenerateCurrentRun(() => this.$emit('chunkUpdateReceived'));
      } catch (error) {
        this.appStore.handleError(error);
        this.chatStore.updateStatus('Error: ' + error, 'error');
      }
    },
  },
  setup() {
    const chatStore = useChatStore(); // Call useMyStore() inside the setup function
    const appStore = useAppStore();
    const assistants = useAssistants();

    return {chatStore, appStore, assistants};
  },
};
</script>

