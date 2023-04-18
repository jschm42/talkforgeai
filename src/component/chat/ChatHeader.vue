<template>
  <div id="prompt-configuration-panel" class="p-2">
    <select id="prompt-configuration-select" v-model="selectedPersonaName" :disabled="isDisabled"
            aria-label="Default select example"
            class="form-select"
            @change="changePersona">
      <option v-for="(person, index) in persona" :key="index" :value="person.name">{{ person.name }} -
        {{ person.description }}
      </option>
    </select>
  </div>

</template>

<script>

import {useChatStore} from '../../store/chat-store';

export default {
  name: 'ChatHeader',
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function

    return {store};
  },
  data() {
    return {
      selectedPersonaName: this.store.chat.personaName,
      persona: this.store.persona,
      selectedIndex: -1,
    };
  },
  mounted() {

  },
  computed: {
    isDisabled() {
      return !this.store.chat.configHeaderEnabled;
    },
  },
  methods: {
    changePersona($event) {
      this.selectedIndex = $event.target.selectedIndex;
      console.log('EVENT', this.selectedIndex);
      if (this.selectedIndex > -1) {
        this.store.changePersona(this.store.persona[this.selectedIndex].name);
      }
    },
  },
  updated($event) {
    console.log('ChatHeader updated!', this.store.chat);
  },
};
</script>

<style scoped>

</style>
