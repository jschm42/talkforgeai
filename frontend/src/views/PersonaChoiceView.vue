<template>
  <div class="container p-3">
    <div class="row header">
      <div class="header">
        <img class="logo" src="@/assets/logo.png" title="Talkforge AI">
      </div>
    </div>
    <div class="row scrollable-persona-list">

      <div v-for="persona in personaList" :key="persona.personaId" class="col-md-3">
        <div class="card mb-3 persona-card"
             style="max-width: 300px;" @click.prevent="onPersonaSelected(persona)">
          <div class="row g-0">
            <div class="col-md-4">
              <img v-if="isShowPersonaImage(persona)" :alt="persona.name" :src="persona.imageUrl"
                   class="persona-icon"/>
              <i v-else class="fs-2 bi bi-robot robot-icon"></i>
            </div>
            <div class="col-md-6">
              <div class="card-body">
                <h5 class="card-title">{{ persona.name }}</h5>
                <!--                <p class="card-text"></p>-->
              </div>
            </div>

            <div class="col-2 d-inline-flex flex-row-reverse my-2 p-2">
              <i class="bi bi-pencil" role="button"></i>
            </div>
          </div>

          <div class="row g-0 p-3">
            <small class="text-body-secondary">{{ persona.description }}</small>
          </div>

        </div>

      </div>

      <div class="col-md-3">
        <div class="card bg-gradient text-center persona-card" role="button" @click.prevent="onCreateNewPersona">
          <div class="card-body">
            <h5 class="card-title">
              Create new persona...
            </h5>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import {defineComponent} from 'vue';
import {useChatStore} from '@/store/chat-store';

export default defineComponent({
  components: {},
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
    personaList() {
      return this.store.personaList;
    },
  },
  methods: {
    isShowPersonaImage(persona) {
      return !!persona.imageUrl;
    },
    onPersonaSelected(persona) {
      this.$router.push({name: 'chat', params: {personaId: persona.personaId}});
    },
    onCreateNewPersona() {
      console.log('onCreateNewPersona');
      this.$router.push({name: 'persona-create'});
    },
  },
  mounted() {
    this.store.readPersona().then(() => {
    });
  },
});
</script>

<style scoped>
.robot-icon {
  margin-right: 8px;
}

.persona-card {
  min-width: 200px;
  min-height: 170px;
}

.persona-icon {
  width: 64px;
  height: 64px;
}

.active {
  border: 2px solid #007BFF; /* blue border */
  background: #2c3e50; /* grey-ish background */
}

.scrollable-persona-list {
  height: 80vh;
  overflow-y: auto;
}

.logo {
  width: 10em;
  height: 10em;
  margin-left: 8px;
}

.header {
  margin-bottom: 1em;
}

</style>
