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

<template>
  <div class="container p-3">
    <div class="row header">
      <div class="header">
        <img alt="Talkforge AI" class="logo" src="@/assets/logo.png" title="Talkforge AI">
      </div>
    </div>
    <div class="row scrollable-persona-list">

      <div v-for="assistant in assistantList" :key="assistant.id"
           class="col-md-3 assistant-element">
        <assistant-element :assistant="assistant"></assistant-element>
      </div>

      <div class="col-md-3">
        <div class="card bg-gradient text-center create-card" role="button"
             @click.prevent="onCreateNewPersona">
          <div class="card-body">
            <h5 class="card-title">
              Create new assistant...
            </h5>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import {defineComponent} from 'vue';
import {useChatStore} from '@/store/chat-store';
import AssistantElement from '@/components/choice/AssistantElement.vue';
import {useAppStore} from '@/store/app-store';

export default defineComponent({
  components: {AssistantElement},
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function
    const appStore = useAppStore();
    return {store, appStore};
  },
  data() {
    return {
      isEntrySelected: false,
    };
  },
  computed: {
    assistantList() {
      return this.store.assistantList;
    },
  },
  methods: {

    isShowAssistantImage(assistant) {
      return !!assistant.image_path;
    },

    onCreateNewPersona() {
      this.$router.push({name: 'persona-create'});
    },

  },
  async mounted() {
    try {
      await this.store.syncAssistants();
    } catch (error) {
      console.error(error);
      this.appStore.addError(error);
    }
  },
});
</script>

<style scoped>
.scrollable-persona-list {
  height: 75vh;
  overflow-y: auto;
}

.create-card {
  width: 200px;
  height: 180px;
}

.logo {
  height: 10em;
  margin-left: 8px;
}

.header {
  margin-bottom: 1em;
}

</style>
