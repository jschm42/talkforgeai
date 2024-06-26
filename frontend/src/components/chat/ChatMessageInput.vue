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
  <div class="input-group">
    <div class="input-group-text">
      <whisper-component @onReceiveText="onReceiveWhisperText"></whisper-component>
    </div>
    <textarea ref="promptInputArea" v-model="prompt" :disabled="isInputLocked"
              class="form-control shadow"
              placeholder="Enter prompt..."
              rows="5"
              @keyup.enter.exact="submit"></textarea>
    <div id="submit-button" class="input-group-text">
      <i class="bi bi-send-check-fill" role="button" style="font-size: 2em" @click="submit"></i>
    </div>
  </div>
</template>


<script>

import {useChatStore} from '@/store/chat-store';
import {useAppStore} from '@/store/app-store';
import WhisperComponent from '@/components/common/WhisperComponent.vue';
import {nextTick} from 'vue';
import hljs from 'highlight.js';
import {useAssistants} from '@/composable/use-assistants';

export default {
  name: 'ChatMessageInput',
  components: {WhisperComponent},
  setup() {
    const chatStore = useChatStore(); // Call useMyStore() inside the setup function
    const appStore = useAppStore();
    const assistants = useAssistants();

    return {chatStore, appStore, assistants};
  },
  data() {
    return {
      prompt: '',
      isInputLocked: false,
    };
  },
  methods: {
    async submit() {
      this.isInputLocked = true;

      this.chatStore.updateStatus('Thinking...', 'info');
      try {
        this.$emit('chunkUpdateReceived');
        await this.assistants.submitUserMessage(this.prompt,
            () => this.$emit('chunkUpdateReceived'));
        this.$emit('chunkUpdateReceived');
      } catch (error) {
        this.appStore.handleError(error);
        this.chatStore.updateStatus('Error: ' + error, 'error');
      } finally {
        await nextTick();
        hljs.highlightAll();
        this.isInputLocked = false;
      }

      console.log('Sending submitResultReceived');
      this.$emit('submitResultReceived');
      this.prompt = '';
    },

    async onReceiveWhisperText(text) {
      console.log('onReceiveWhisperText: ', text);
      this.prompt = text;
      await this.submit();
    },
  },
};
</script>

<style scoped>

</style>
