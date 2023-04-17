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

<script>
import Store from '../../store/store';

export default {
  name: 'ChatHeader',
  data() {
    return {
      selectedPersonaName: Store.state.chat.personaName,
      persona: Store.state.persona,
      selectedIndex: -1,
    };
  },
  methods: {
    changePersona($event) {
      this.selectedIndex = $event.target.selectedIndex;
      console.log('EVENT', this.selectedIndex);
      if (this.selectedIndex > -1) {
        Store.mutations.persona.change(Store.state.persona[this.selectedIndex].name);
      }
    },
  },
};
</script>

<style scoped>

</style>
