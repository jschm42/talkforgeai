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

export default {
  name: 'ChatMessage',
  components: {},
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function
    return {store};
  },
  data() {
    return {};
  },
  props: {
    message: Object,
    messageIndex: Number,
  },
  computed: {
    errorMessage() {
      return this.store.currentStatusMessage;
    },
    personaImage() {
      // Find the assistant in the store
      const assistantId = this.message.assistant_id;
      const assistant = this.store.assistantList.find(a => a.id === assistantId);
      if (assistant) {
        return this.store.getAssistantImageUrl(assistant.image_path);
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
      const assistantId = this.message.assistant_id;
      const assistant = this.store.assistantList.find(a => a.id === assistantId);
      if (assistant && assistant.image_path) {
        return assistant.image_path;
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
  <!--
  <div :class="messageClass" class="card m-1 p-1 shadow">
    <div class="d-flex flex-row">
      <div class="">
        <i v-if="isUser" class="fs-1 bi bi-person"></i>
        <img v-else-if="isAssistant && hasProfileImage" :src="personaImage" alt="Assistant"
             class="persona-icon">
        <i v-else class="fs-1 bi bi-robot robot-icon"></i>
      </div>
      <div class="flex-grow-1">
        <div class="card-body">
          <div v-if="getMessageStatusType() === 'running'" class="spinner-grow text-primary"
               role="status">
          </div>
          <span v-else-if="getMessageStatusType() === 'error'">
            <i class="bi bi-exclamation-lg bg-danger"></i>
            {{ errorMessage }}
          </span>
          <div v-else class="card-text text-start" v-html="getContent()"></div>
        </div>
      </div>
      <div class="text-end">
        <chat-message-text-to-speech v-if="isAssistant" ref="chatMessageAudioPlayerRef"
                                     :message="message"></chat-message-text-to-speech>
      </div>
    </div>
    <footer>

    </footer>
  </div>
  -->
  <v-container class="message-container">
    <v-row dense>
      <v-col cols="12">
        <v-card
            :loading="getMessageStatusType() === 'running'"
            class="p-1"
        >
          <div class="d-flex flex-no-wrap justify-space-between">


            <i v-if="isUser" class="fs-1 bi bi-person"></i>
            <v-avatar v-else-if="isAssistant && hasProfileImage"
                      :image="personaImage"
                      class="ma-3"
                      size="80"
            >
            </v-avatar>
            <i v-else class="fs-1 bi bi-robot robot-icon"></i>

            <div>
              <span v-if="getMessageStatusType() === 'error'">
                <i class="bi bi-exclamation-lg bg-danger"></i>
                  {{ errorMessage }}
              </span>
              <v-card-text v-else v-html="getContent()"></v-card-text>

              <v-card-actions>
                <v-btn
                    class="ms-2"
                    size="small"
                    variant="outlined"
                >
                  START RADIO
                </v-btn>
              </v-card-actions>
            </div>

          </div>
        </v-card>
      </v-col>
    </v-row>
  </v-container>

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

.message-container {
  max-width: 100%;
}

</style>
