<template>
  <!-- Align the badges horizontally -->

  <div class="container p-0 m-0">
    <div class="d-flex flex-row">
      <div v-for="item in properties" :key="item.key" class="p-1">
        <span :class="badgeColor(item.key)" class="badge rounded-pill">{{ item.key }}: {{ item.value }}</span>
        <!--      <span class="badge rounded-pill bg-primary text-light">{{ properties[key] }}</span>-->
      </div>
    </div>
  </div>
</template>

<script>

import {useChatStore} from '@/store/chat-store';

const allowedKeys = ['model', 'temperature', 'top_p'];

export default {
  name: 'ChatConfig',
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function

    return {store};
  },
  data() {
    return {};
  },
  computed: {

    properties() {
      let result = [];

      if (this.store.selectedPersona.properties) {
        Object.keys(this.store.selectedPersona.properties).filter((key) => {
          return allowedKeys.indexOf(key) !== -1;
        }).forEach((key) => {
          result.push({key: key, value: this.store.selectedPersona.properties[key]});
        });
      }

      return result;
    },
  },
  methods: {
    badgeColor(key) {
      switch (key) {
        case 'chatgpt_model':
          return 'text-bg-primary';
        case 'chatgpt_temperature':
          return 'text-bg-success';
        default:
          return 'text-bg-secondary';
      }
    },
  },
};
</script>

<style scoped>

</style>
