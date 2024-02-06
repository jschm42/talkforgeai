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
  <div class="d-flex flex-column chat-container gx-0">
    <div ref="entries" class="flex-grow-1 vertical-scrollbar no-horizontal-scrollbar ">
      <!--      <div class="mx-0 p-2">-->
      <ChatMessage v-for="(message, index) in store.threadMessages" ref="chatMessageRef"
                   v-bind:key="index"
                   :message="message" :messageIndex="index"></ChatMessage>
      <!--      </div>-->
    </div>
    <!-- Input Section -->

    <div class="flex-shrink-0">
      <ChatControl @submit-result-received="submitResultReceived"
                   @chunk-update-received="chunkUpdateReceived"></ChatControl>
    </div>
  </div>
</template>

<script>
import ChatControl from './ChatControl.vue';
import ChatMessage from './ChatMessage.vue';
import {useChatStore} from '@/store/chat-store';
import {useAppStore} from '@/store/app-store';

export default {
  name: 'ChatContainer',
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function
    const appStore = useAppStore();
    return {
      store, appStore,
    };
  },
  data() {
    return {};
  },
  components: {ChatControl, ChatMessage},
  mounted() {
    this.populateVoices();
  },
  methods: {
    async submitResultReceived() {
      console.log('Submit Result Received');

      if (this.store.isAutoSpeak && this.store.isTTSEnabled) {
        const lastChatMessage = this.$refs.chatMessageRef.slice(-1)[0];
        console.log('Auto speaking last Chat-Message:');
        await lastChatMessage.playAudio();
      }
    },
    populateVoices() {
      const voices = speechSynthesis.getVoices();
      if (voices.length > 0) {
        console.log('Voices already loaded');
        this.speechApiVoices = voices;
      } else {
        console.log('Voices not loaded, waiting for onvoiceschanged');
        speechSynthesis.onvoiceschanged = () => {
          console.log('Voices loaded.');
        };
      }
    },
    chunkUpdateReceived() {
      console.log('Chunk Update Received!');
      // Scroll $refs.entries to bottom
      this.$refs.entries.scrollTop = this.$refs.entries.scrollHeight;
      //this.$refs.entries.scrollTop = this.$refs.entries.scrollHeight;
    },
  },
};
</script>

<style scoped>
.vertical-scrollbar {
  overflow-y: auto;
}

.no-horizontal-scrollbar {
  overflow-x: hidden;
}

.chat-container {
  height: 93vh;
}

@media only screen and (min-width: 993px ) {
  .chat-container {
    height: 100vh;
  }
}

</style>
