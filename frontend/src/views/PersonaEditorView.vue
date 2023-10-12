<template>
  <div class="container">
    <h2>Persona Editor</h2>
    <form class="form-panel" @submit.prevent="handleSubmit">

      <ul id="personaTab" class="nav nav-tabs" role="tablist">
        <li class="nav-item">
          <button id="home-tab" aria-controls="home-tab-pane" aria-selected="true" class="nav-link active"
                  data-bs-target="#main-tab-pane" data-bs-toggle="tab" role="tab" type="button">Home
          </button>
        </li>
        <li class="nav-item">
          <button id="model-tab" aria-controls="model-tab-pane" aria-selected="true" class="nav-link"
                  data-bs-target="#model-tab-pane" data-bs-toggle="tab" role="tab" type="button">Model
          </button>
        </li>
        <li class="nav-item">
          <button id="features-tab" aria-controls="features-tab-pane" aria-selected="true" class="nav-link"
                  data-bs-target="#features-tab-pane" data-bs-toggle="tab" role="tab" type="button">Features
          </button>
        </li>
      </ul>

      <div id="personaTabContent" class="tab-content">

        <div id="main-tab-pane" aria-labelledby="main-tab" class="tab-pane fade show active" role="tabpanel"
             tabindex="0">
          <persona-tab-main ref="mainTabPane"></persona-tab-main>

        </div>

        <div id="model-tab-pane" aria-labelledby="model-tab" class="tab-pane fade" role="tabpanel"
             tabindex="0">
          <h3>Model</h3>
        </div>

        <div id="features-tab-pane" aria-labelledby="features-tab" class="tab-pane fade" role="tabpanel"
             tabindex="0">
          <h3>Features</h3>
        </div>

      </div>


      <button class="btn btn-primary" type="submit">Create persona</button>


    </form>
  </div>
</template>

<script>
import {defineComponent} from 'vue';
import {useChatStore} from '@/store/chat-store';
import PersonaTabMain from '@/components/persona/PersonaTabMain.vue';

export default defineComponent({
  components: {PersonaTabMain},
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function
    return {store};
  },
  data() {
    return {};
  },
  computed: {},
  methods: {
    handleSubmit() {
      const mainTabPane = this.$refs.mainTabPane;
      console.log('MainTabPane', mainTabPane.$data);

      console.log('handleSubmit');
    },
  },
  mounted() {
  },
});
</script>

<style scoped>
.form-panel {
  color: white;
  text-align: left;
}

</style>
