<script>

import {useChatStore} from '@/store/chat-store';
import Role from '@/store/to/role';
import hljs from 'highlight.js';
import ChatMessageAudioPlayer from '@/components/ChatMessageAudioPlayer.vue';
import ChatMessage from '@/store/to/chat-message';

export default {
  name: 'ChatMessage',
  components: {ChatMessageAudioPlayer},
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
        'bg-info': this.message.role === Role.USER,
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
  },
  methods: {
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
        return this.store.currentStatusMessage;
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
        <chat-message-audio-player :message="this.message"></chat-message-audio-player>
      </div>
    </div>
    <footer>
      <strong class="mx-1">{{ getMessageStatus() }}</strong>
      <div v-if="getMessageStatus() !== ''" class="spinner-border spinner-border-sm" role="status">
        <span class="sr-only">{{ getMessageStatus() }}</span>
      </div>
    </footer>
  </div>

</template>

<style scoped>
.role-user {

}

.role-assistant {

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
