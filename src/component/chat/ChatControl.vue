<template>
  <div class="p-2 m-1 rounded shadow input-container">
    <!-- Tabs -->
    <ul id="pills-tab" class="nav nav-pills mb-3" role="tablist">

      <li class="nav-item" role="presentation">
        <button id="pills-message-tab" aria-controls="pills-message" aria-selected="true"
                class="nav-link active"
                data-bs-target="#pills-message" data-bs-toggle="pill" role="tab" type="button">ChatMessage
        </button>
      </li>

      <li class="nav-item" role="presentation">
        <button id="pills-system-tab" aria-controls="pills-system" aria-selected="true"
                class="nav-link"
                data-bs-target="#pills-system" data-bs-toggle="pill" role="tab" type="button">System
        </button>
      </li>

      <li class="nav-item" role="presentation">
        <button id="pills-parameters-tab" aria-controls="pills-parameters" aria-selected="false"
                class="nav-link"
                data-bs-target="#pills-parameters" data-bs-toggle="pill" role="tab" type="button">Parameters
        </button>
      </li>
    </ul>
    <div id="pills-tabContent" class="tab-content">
      <div id="pills-message" aria-labelledby="pills-message-tab" class="tab-pane fade show active"
           role="tabpanel" tabindex="0">
        <Input @submit-result-received="submitResultReceived"></Input>
      </div>

      <div id="pills-system" aria-labelledby="pills-system-tab" class="tab-pane fade" role="tabpanel"
           tabindex="0">
        <System></System>
      </div>
      <div id="pills-parameters" aria-labelledby="pills-parameters-tab" class="tab-pane fade" role="tabpanel"
           tabindex="0">
        <Parameters></Parameters>
      </div>
    </div>
    <!-- Ende Tabs -->
  </div>
</template>

<script>
import Parameters from './Parameters.vue';
import System from './System.vue';
import Input from './Input.vue';

export default {
  name: 'ChatControl',
  components: {Input, Parameters, System},
  methods: {
    submitResultReceived() {
      this.$emit('submitResultReceived');
    },
  },
};
</script>

<style scoped>

</style>
