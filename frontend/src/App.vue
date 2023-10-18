<template>
  <router-view/>
</template>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
</style>


<script>
import {useChatStore} from '@/store/chat-store';
import {usePersonaFormStore} from '@/store/persona-form-store';

export default {
  name: 'ChatContainer',
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function
    const personaStore = usePersonaFormStore();

    return {
      store,
      personaStore,
    };
  },
  data() {
    return {};
  },
  methods: {},
};
</script>
