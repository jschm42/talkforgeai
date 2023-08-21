<template>
  <div id="prompt-configuration-panel" class="p-2">
    <!-- TODO Check if still needed -->
    {{ infoString }}
  </div>

</template>

<script>

import {useChatStore} from '@/store/chat-store';

export default {
  name: 'ChatHeader',
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function

    return {store};
  },
  data() {
    return {};
  },
  mounted() {
    this.store.readPersona();
  },
  computed: {
    infoString() {
      return `${this.store.selectedPersona.name} - ${this.store.selectedSessionId}`;
    },
    isDisabled() {
      return !this.store.chat.configHeaderEnabled;
    },
    personaList() {
      return this.store.personaList;
    },
    selectedPersona() {
      return this.store.selectedPersona;
    },
  },
  methods: {
    isShowPersonaImage(persona) {
      return !!persona.imageUrl;
    },
    onPersonaSelected(persona) {
      this.store.selectedPersona = persona;
    },
  },
};
</script>

<style scoped>
.robot-icon {
  margin-right: 8px;
}

.persona-icon {
  width: 32px;
  height: 32px;
}
</style>
