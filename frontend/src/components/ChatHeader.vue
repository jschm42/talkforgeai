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
               class="persona-icon mx-3"
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
          <img v-if="isShowPersonaImage(persona)" :alt="persona.name" :src="persona.imageUrl"
               class="persona-icon"/>
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
    return {};
  },
  mounted() {
    this.store.readPersona();
  },
  computed: {
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
