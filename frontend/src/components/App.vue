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
  <v-app>
    <router-view/>

    <error-alerts></error-alerts>
  </v-app>
</template>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  height: 100%;
}

.text-gradient-silver {
  background-image: linear-gradient(to top, #555555 0%, #ffffff 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  display: inline-block;
  -webkit-text-fill-color: transparent; /* For Safari compatibility */
}

h1 {
  font-size: 1.5rem;
}

h2 {
  font-size: 1.25rem;
}

</style>


<script>
import {useChatStore} from '@/store/chat-store';
import {usePersonaFormStore} from '@/store/persona-form-store';
import ErrorAlerts from '@/components/common/ErrorAlerts.vue';

export default {
  name: 'ChatContainer',
  components: {ErrorAlerts},
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function
    const personaStore = usePersonaFormStore();

    return {
      store,
      personaStore,
    };
  },
  data() {
    return {};
  },
  methods: {},
};
</script>
