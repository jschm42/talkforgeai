<!--
  - Copyright (c) 2023 Jean Schmitz.
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

<script>

import {useChatStore} from '@/store/chat-store';
import Role from '@/store/to/role';
import ChatMessageTextToSpeech from '@/components/ChatMessageTextToSpeech.vue';
import {ThreadMessage} from '@/store/to/thread';

export default {
  name: 'ChatMessage',
  components: {ChatMessageTextToSpeech},
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function
    return {store};
  },
  data() {
    return {};
  },
  props: {
    message: ThreadMessage,
    messageIndex: Number,
  },
  computed: {
    personaImage() {
      return this.store.selectedAssistant.imageUrl;
    },
    messageClass() {
      return {
        'role-user': this.message.role === Role.USER,
        'role-assistantEntity': this.message.role === Role.ASSISTANT,
      };
    },
    isAssistant() {
      return this.message.role === Role.ASSISTANT && !!this.store.selectedAssistant.imagePath;
    },
    isUser() {
      return this.message.role === Role.USER;
    },
    // isFunctionCall() {
    //   return this.message.function_call;
    // },
    // isFunctionResponse() {
    //   return this.message.name && this.message.content;
    // },
    isSpeakable() {
      return true;
    },
    avatarImageClass() {
      return {
        'bi-robot': this.message.role === Role.ASSISTANT,
        'bi-person-fill': this.message.role === Role.USER,
      };
    },
    statusMessage() {
      return this.store.currentStatusMessage;
    },
    statusMessageType() {
      return this.store.currentStatusMessageType;
    },
  },
  methods: {
    async playAudio() {
      await this.$refs.chatMessageAudioPlayerRef.playAudio();
    },
    getContent() {
      // if (this.message.function_call) {
      //   const func = this.message.function_call;
      //   const argumentsHighlighted = hljs.highlight(func.arguments, {language: 'json'}).value;
      //
      //   return `<strong>${func.name}(<p>${argumentsHighlighted}</p>)</strong>`;
      // }

      return this.message.content[0].text.value;
    },
    getMessageStatus() {
      if (this.messageIndex === this.store.maxMessageIndex) {
        return this.statusMessage;
      }
      return '';
    },
    getMessageStatusType() {
      if (this.messageIndex === this.store.maxMessageIndex) {
        return this.statusMessageType;
      }
      return '';
    },
  },
};
</script>

<template>
  <div :class="messageClass" class="card m-1 p-1 shadow">
    <div class="row">
      <div class="col-md-1">
        <i v-if="isUser" class="fs-1 bi bi-person"></i>
        <img v-else-if="isAssistant" :src="personaImage" alt="Assistant" class="persona-icon">
        <!--        <i v-else-if="isFunctionCall" class="fs-1 bi bi-gear"></i>-->
        <!--        <i v-else-if="isFunctionResponse" class="fs-1 bi bi-arrow-return-left"></i>-->
        <i v-else class="fs-1 bi bi-robot robot-icon"></i>
      </div>
      <div class="col-md-10">
        <div class="card-body">
          <div v-if="getMessageStatusType() === 'running'" class="spinner-grow text-primary" role="status">
          </div>
          <i v-else-if="getMessageStatusType() === 'error'" class="bi bi-exclamation-lg bg-danger"></i>
          <div v-else class="card-text text-start" v-html="getContent()"></div>
        </div>
      </div>
      <div class="col-md-1 text-end">
        <chat-message-text-to-speech v-if="isAssistant" ref="chatMessageAudioPlayerRef"
                                     :message="message"></chat-message-text-to-speech>
      </div>
    </div>
    <footer>

    </footer>
  </div>

</template>

<style scoped>
.role-user {
  background-color: #2c3e50;
}

.role-assistantEntity {
//background-color: #000000;
}

.role-icon {
  font-size: 2em;
}

.persona-icon {
  width: 64px;
  height: 64px;
}

.message-icon {
  font-size: 1.5em;
}

.code-word {
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 0.25rem;
  /*font-family: "Source Code Pro", monospace;*/
  font-family: 'Courier New', monospace
}

footer {
  font-style: italic;
}

.robot-icon {
  color: darksalmon;
}

</style>
