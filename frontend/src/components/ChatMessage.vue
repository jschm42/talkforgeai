<script>

import {useChatStore} from '@/store/chat-store';
import Role from '@/store/to/role';
import hljs from 'highlight.js';
import ChatMessage from '@/store/to/chat-message';
import ChatMessageTextToSpeech from '@/components/ChatMessageTextToSpeech.vue';

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
    message: ChatMessage,
    messageIndex: Number,
  },
  computed: {
    personaImage() {
      return this.store.selectedPersona.imageUrl;
    },
    messageClass() {
      return {
        'role-user': this.message.role === Role.USER,
        'role-assistant': this.message.role === Role.ASSISTANT,
      };
    },
    isAssistant() {
      return this.message.role === Role.ASSISTANT;
    },
    isUser() {
      return this.message.role === Role.USER && !this.message.name;
    },
    isFunctionCall() {
      return this.message.function_call;
    },
    isFunctionResponse() {
      return this.message.name && this.message.content;
    },
    isSpeakable() {
      return !this.isFunctionCall;
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
      if (this.message.function_call) {
        const func = this.message.function_call;
        const argumentsHighlighted = hljs.highlight(func.arguments, {language: 'json'}).value;

        return `<strong>${func.name}(<p>${argumentsHighlighted}</p>)</strong>`;
      }

      return this.message.content;
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
        <i v-else-if="isFunctionCall" class="fs-1 bi bi-gear"></i>
        <i v-else-if="isFunctionResponse" class="fs-1 bi bi-arrow-return-left"></i>
        <i v-else class="fs-1 bi bi-robot"></i>
      </div>
      <div class="col-md-10">
        <div class="card-body">
          <div class="card-text text-start" v-html="getContent()"></div>
        </div>
      </div>
      <div class="col-md-1 text-end">
        <chat-message-text-to-speech v-if="isAssistant" ref="chatMessageAudioPlayerRef"
                                     :message="this.message"></chat-message-text-to-speech>
      </div>
    </div>
    <footer>
      <strong class="mx-1">{{ getMessageStatus() }}</strong>
      <div v-if="getMessageStatusType() === 'running'" class="spinner-border spinner-border-sm" role="status">
        <span class="sr-only">{{ getMessageStatus() }}</span>
      </div>
      <i v-else-if="getMessageStatusType() === 'error'" class="bi bi-exclamation-lg bg-danger"></i>
    </footer>
  </div>

</template>

<style scoped>
.role-user {
  background-color: #2c3e50;
}

.role-assistant {
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

</style>
