<template>
  <div id="prompt-configuration-panel" class="p-2">
    <div class="dropdown">
      <button
        id="dropdownMenuButton"
        :disabled="isDisabled"
        aria-expanded="false"
        aria-haspopup="true"
        class="btn btn-secondary dropdown-toggle"
        data-bs-toggle="dropdown"
        type="button"
      >
        <div v-if="selectedPersona">
          <img v-if="isShowPersonaImage(selectedPersona)"
               :alt="selectedPersona.name"
               :src="selectedPersona.imageUrl"
               class="button-image mx-3"
          />
          <i v-else class="fs-2 bi bi-robot robot-icon"></i>
          <span>{{ selectedPersona.name }} - {{ selectedPersona.description }}</span>
        </div>
        <span v-else>Choose a persona</span>
      </button>
      <div aria-labelledby="dropdownMenuButton" class="dropdown-menu">
        <a v-for="persona in personaList" :key="persona.id"
           class="dropdown-item"
           href="#"
           @click.prevent="onPersonaSelected(persona)">
          <img v-if="isShowPersonaImage(persona)" :alt="persona.name" :src="persona.imageUrl"/>
          <i v-else class="fs-2 bi bi-robot robot-icon"></i>
          {{ persona.name }} - {{ persona.description }}
        </a>
      </div>
    </div>
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
    return {
      selectedPersona: null,
    };
  },
  mounted() {
    this.store.readPersona();
  },
  computed: {
    isDisabled() {
      return !this.store.chat.configHeaderEnabled;
    },
    personaList() {
      return this.store.persona;
    },
  },
  methods: {
    isShowPersonaImage(persona) {
      return !!persona.imageUrl;
    },
    onPersonaSelected(persona) {
      console.log('ON PERSONA SELECT', persona);
      this.store.changePersona(persona.name);
      this.selectedPersona = persona;
    },
  },
  updated($event) {
    console.log('ChatHeader updated!', this.store.chat);
  },
};
</script>

<style scoped>
.robot-icon {
  margin-right: 8px;
}
</style>
