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
    <component :is="currentViewComponent"></component>
  </v-app>
</template>

<script>
import {computed, onMounted} from 'vue';
import {useAppStore} from '@/store/app-store';
import {useDisplay} from 'vuetify';
import ChoiceMobile from '@/components/choice/ChoiceMobile.vue';
import ChoiceDesktop from '@/components/choice/ChoiceDesktop.vue';
import {useAssistants} from '@/composable/use-assistants';

export default {
  components: {ChoiceDesktop, ChoiceMobile},
  setup() {
    const {mobile} = useDisplay();
    const appStore = useAppStore();
    const assistants = useAssistants();

    const currentViewComponent = computed(() => {
      // You can adjust the breakpoint here according to your needs

      const isMobile = mobile.value;
      return isMobile ? 'ChoiceMobile' : 'ChoiceDesktop';
    });

    onMounted(async () => {
      try {
        await assistants.syncAssistants();
      } catch (error) {
        appStore.handleError(error);
      }
    });

    return {
      currentViewComponent,
    };
  },
};

</script>

<style scoped>

</style>
