<template>
  <div id="prompt-configuration-panel" class="p-2">
    <select id="prompt-configuration-select" v-model="selectedPersonaName" aria-label="Default select example"
            class="form-select"
            @change="changePersona">
      <option v-for="(person, index) in persona" :key="index" :value="person.name">{{ person.name }} -
        {{ person.description }}
      </option>
    </select>
  </div>

</template>

<script lang="ts">

import {useChatStore} from '../../store/piniaStore';

export default {
  name: 'ChatHeader',
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function

    return {store};
  },
  data() {
    return {
      selectedPersonaName: this.store.personaName,
      persona: this.store.persona,
      selectedIndex: -1,
    };
  },
  methods: {
    changePersona($event) {
      this.selectedIndex = $event.target.selectedIndex;
      console.log('EVENT', this.selectedIndex);
      if (this.selectedIndex > -1) {
        this.store.changePersona(this.store.chat.persona[this.selectedIndex].name);
      }
    },
  },
};
</script>

<style scoped>

</style>
