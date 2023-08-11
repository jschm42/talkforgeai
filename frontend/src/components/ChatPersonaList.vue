<template>
  <div class="p-3 full-height vertical-scrollbar shadow">

    <div class="list-group list-group-flush border-bottom">

      <div v-for="persona in personaList" :key="persona.personaId">

        <div class="card mb-3"
             style="max-width: 300px;"
             v-bind:class="{active: persona === selectedPersona}" @click.prevent="onPersonaSelected(persona)">
          <div class="row g-0">
            <div class="col-md-4">
              <img v-if="isShowPersonaImage(persona)" :alt="persona.name" :src="persona.imageUrl"
                   class="persona-icon"/>
              <i v-else class="fs-2 bi bi-robot robot-icon"></i>
            </div>
            <div class="col-md-8">
              <div class="card-body">
                <h5 class="card-title">{{ persona.name }}</h5>
                <p class="card-text"><small class="text-body-secondary">{{ persona.description }}</small></p>
              </div>
            </div>
          </div>
        </div>

      </div>

    </div>

  </div>
</template>

<style scoped>
.robot-icon {
  margin-right: 8px;
}

.persona-icon {
  width: 64px;
  height: 64px;
}

.active {
  border: 2px solid #007BFF; /* blue border */
  background: #2c3e50; /* grey-ish background */
}
</style>

<script>
import {useChatStore} from '@/store/chat-store';

export default {
  name: 'ChatPersonaList',
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function
    return {store};
  },
  data() {
    return {
      isEntrySelected: false,
    };
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
  mounted() {
    this.store.readPersona();
  },
};

</script>

