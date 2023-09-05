<template>
  <div class="container">
    <div class="row text-start">
      <div class="col-2 align-items-start">
        <i class="bi bi-arrow-90deg-left back-button" role="button" @click="onClickBack"></i>
      </div>
      <div class="col-2">
        <img v-if="isShowPersonaImage(selectedPersona)" :alt="selectedPersona.name" :src="selectedPersona.imageUrl"
             class="persona-icon"/>
        <i v-else class="fs-2 bi bi-robot robot-icon"></i>
      </div>

      <div class="col-8">
        <div class="row persona-name mx-1">
          {{ personaName }}
        </div>
        <div class="row persona-description mx-1">
          {{ personaDescription }}
        </div>
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
  computed: {
    personaName() {
      return `${this.store.selectedPersona.name}`;
    },
    personaDescription() {
      return `${this.store.selectedPersona.description}`;
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
    onClickBack() {
      this.$router.push('/');
    },
  },
};
</script>

<style scoped>
.robot-icon {
  margin-right: 8px;
}

.persona-name {
  font-size: 1.5rem;
  font-weight: 500;
  color: #6c757d;
}

.persona-description {
  font-size: 0.9em;
  font-style: italic;
  color: #6c757d;
  overflow: hidden;
}

.back-button {
  font-size: 2.5rem;
  color: #6c757d;
}

.persona-icon {
  width: 4rem;
  height: 4rem;
}
</style>
