<template>

</template>

<script>

import {useChatStore} from '../../store/chat-store';

export default {
  props: {
    entries: {
      type: Array,
      required: true,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function

    return {store};
  },
  computed: {
    selectedPersona() {
      return this.store.session.persona;
    },
  },
  methods: {
    selectOption(entry) {
      console.log('SELECT OPTION', entry);
      this.selectedPersona = entry;
      this.$emit('selected', entry);
    },
    imageUrl(personaImage) {
      return 'file://C:\\users\\jschmitz\\.aitool\\persona\\' + personaImage;
    },
  },
};
</script>

<style>
.dropdown-item {
  display: flex;
  align-items: center;
}

.dropdown-item img {
  width: 32px;
  height: 32px;
  margin-right: 10px;
}

.button-image {
  width: 32px;
  height: 32px;
}
</style>

