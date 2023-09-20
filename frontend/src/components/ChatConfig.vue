<template>
  <!-- Align the badges horizontally -->

  <div class="container">
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
          return key === 'model' || key === 'temperature';
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
        case 'model':
          return 'text-bg-primary';
        case 'temperature':
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
