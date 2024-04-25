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

<script>

import {useChatStore} from '@/store/chat-store';
import Role from '@/store/to/role';
import ChatMessageTextToSpeech from '@/components/chat/ChatMessageTextToSpeech.vue';
import {useAssistants} from '@/composable/use-assistants';
import {ThreadMessage} from '@/store/to/thread';

export default {
  name: 'ChatMessage',
  components: {ChatMessageTextToSpeech},
  setup() {
    const chatStore = useChatStore(); // Call useMyStore() inside the setup function
    const assistants = useAssistants();
    return {chatStore, assistants};
  },
  data() {
    return {};
  },
  props: {
    message: ThreadMessage,
    messageIndex: Number,
  },
  computed: {
    errorMessage() {
      return this.chatStore.currentStatusMessage;
    },
    personaImage() {
      // Find the assistant in the store
      const assistantId = this.message.assistant_id;
      const assistant = this.chatStore.assistantList.find(a => a.id === assistantId);
      if (assistant) {
        return this.assistants.getAssistantImageUrl(assistant.image_path);
      }
      return '';
    },
    messageClass() {
      return {
        'role-user': this.message.role === Role.USER,
        'role-assistantEntity': this.message.role === Role.ASSISTANT,
      };
    },
    hasProfileImage() {
      const assistantId = this.message.assistantId;
      const assistant = this.chatStore.assistantList.find(a => a.id === assistantId);
      if (assistant?.imagePath) {
        return assistant.imagePath;
      }
      return false;
    },
    isAssistant() {
      return this.message.role === Role.ASSISTANT;
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
      return this.chatStore.currentStatusMessage;
    },
    statusMessageType() {
      return this.chatStore.currentStatusMessageType;
    },
  },
  methods: {
    async playAudio() {
      await this.$refs.chatMessageAudioPlayerRef.playAudio();
    },
    getContent() {
      if (this.chatStore.parsedMessages[this.message.id] !== undefined) {
        return this.chatStore.parsedMessages[this.message.id];
      }
      return this.message.content;
    },
    getMessageStatus() {
      if (this.messageIndex === this.chatStore.maxMessageIndex) {
        return this.statusMessage;
      }
      return '';
    },
    getMessageStatusType() {
      if (this.messageIndex === this.chatStore.maxMessageIndex) {
        return this.statusMessageType;
      }
      return '';
    },
  },
};
</script>

<template>
  <v-container class="message-container">
    <v-row dense>
      <v-col cols="12">
        <v-card
            :loading="getMessageStatusType() === 'running'"
            class="p-1"
        >
          <div class="d-flex flex-no-wrap justify-space-between">

            <v-avatar v-if="isUser" class="fs-1" icon="mdi-account" size="100"></v-avatar>
            <v-avatar v-else-if="isAssistant && hasProfileImage"
                      :image="personaImage"
                      class="ma-3"
                      size="80"
            >
            </v-avatar>

            <v-avatar v-else class="fs-1" icon="mdi-robot" size="100">
            </v-avatar>

            <v-card-text v-if="getMessageStatusType() === 'error'">
              <i class="bi bi-exclamation-lg bg-danger"></i>
              {{ errorMessage }}
            </v-card-text>
            <v-card-text v-else v-html="getContent()"></v-card-text>
          </div>

          <v-card-actions v-if="isAssistant">
            <chat-message-text-to-speech ref="chatMessageAudioPlayerRef"
                                         :message="message"></chat-message-text-to-speech>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>

</template>

<style scoped>
footer {
  font-style: italic;
}

.message-container {
  max-width: 100%;
}

</style>
