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
               :src="imageUrl(selectedPersona.personaImage)"
               class="button-image mx-3"
          />
          <i v-else class="fs-2 bi bi-robot robot-icon"></i>
          <span>{{ selectedPersona.name }} - {{ selectedPersona.description }}</span>
        </div>
        <span v-else>Choose a persona</span>
      </button>
      <div aria-labelledby="dropdownMenuButton" class="dropdown-menu">
        <a
          v-for="(persona, index) in personaList"
          :key="index"
          class="dropdown-item"
          href="#"
          @click.prevent="onPersonaSelected(persona)"
        >
          <img v-if="isShowPersonaImage(persona)" :alt="persona.name" :src="imageUrl(persona.personaImage)"/>
          <i v-else class="fs-2 bi bi-robot robot-icon"></i>
          {{ persona.name }} - {{ persona.description }}
        </a>
      </div>
    </div>
  </div>

</template>

<script>

import {useChatStore} from '../../store/chat-store';
import ImageSelect from '../common/ImageSelect.vue';

export default {
  name: 'ChatHeader',
  components: {ImageSelect},
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function

    return {store};
  },
  data() {
    return {
      //selectedPersona: null,
    };
  },
  mounted() {
    this.readPersonaList();
  },
  computed: {
    isDisabled() {
      return !this.store.chat.configHeaderEnabled;
    },
    personaList() {
      return this.store.persona;
    },
    selectedPersona() {
      return this.store.session.persona;
    },
  },
  methods: {
    isShowPersonaImage(persona) {
      return !!persona.personaImage;
    },
    onPersonaSelected(persona) {
      console.log('ON PERSONA SELECT', persona);
      this.store.changePersona(persona.name);
      this.selectedPersona = persona;
    },
    readPersonaList() {
      this.store.readPersonas().then(() => {
        console.log('Personas read done.');
      });
    },
    imageUrl(personaImage) {
      return window.configAPI.getPersonaImagePath(personaImage);
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
