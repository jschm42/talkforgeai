<template>
  <!-- Align the badges horizontally -->

  <div class="container p-0 m-0">
    <div class="d-flex flex-row">
      <div v-for="item in properties" :key="item.key" class="p-1">
        <span :class="badgeColor(item.key)" class="badge rounded-pill">{{ badgeLabel(item.key) }} {{
            item.value
          }}</span>
        <!--      <span class="badge rounded-pill bg-primary text-light">{{ properties[key] }}</span>-->
      </div>
    </div>
  </div>
</template>

<script>

import {useChatStore} from '@/store/chat-store';
import PersonaProperties from '@/service/persona.properties';
import Persona from '@/store/to/persona';

export default {
  name: 'PersonaCompactInfo',
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function

    return {store};
  },
  data() {
    return {
      allowedKeys: [],
    };
  },
  props: {
    persona: {
      type: Persona,
      required: true,
    },
    showModels: {
      type: Boolean,
      default: true,
    },
    showProperties: {
      type: Boolean,
      default: false,
    },
  },
  mounted() {
    if (this.showModels) {
      this.allowedKeys.push(
        PersonaProperties.CHATGPT_MODEL,
      );
    }

    if (this.showProperties) {
      this.allowedKeys.push(
        PersonaProperties.CHATGPT_TEMPERATURE,
        PersonaProperties.CHATGPT_TOP_P,
      );
    }
  },
  computed: {
    properties() {
      let result = [];

      if (this.persona.properties) {
        Object.keys(this.persona.properties).filter((key) => {
          return this.allowedKeys.indexOf(key) !== -1;
        }).forEach((key) => {
          result.push({key: key, value: this.persona.properties[key]});
        });
      }

      return result;
    },
  },
  methods: {
    badgeColor(key) {
      switch (key) {
        case PersonaProperties.CHATGPT_MODEL:
          return 'text-bg-primary';
        case PersonaProperties.CHATGPT_TEMPERATURE:
          return 'text-bg-success';
        case PersonaProperties.ELEVENLABS_MODELID:
          return 'text-bg-warning';
        default:
          return 'text-bg-secondary';
      }
    },
    badgeLabel(key) {
      switch (key) {
        case PersonaProperties.CHATGPT_MODEL:
        case PersonaProperties.ELEVENLABS_MODELID:
          return '';
        case PersonaProperties.CHATGPT_TEMPERATURE:
          return 'Temp: ';
        case PersonaProperties.CHATGPT_TOP_P:
          return 'TopP: ';
        default:
          return 'Unknown';
      }
    },
  },
};
</script>

<style scoped>

</style>
