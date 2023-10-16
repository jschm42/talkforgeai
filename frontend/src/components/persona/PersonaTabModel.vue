<script>
import {defineComponent} from 'vue';
import {storeToRefs} from 'pinia';
import {usePersonaFormStore} from '@/store/persona-form-store';

export default defineComponent({
  name: 'PersonaTabModel',
  setup() {
    const {personaForm} = storeToRefs(usePersonaFormStore());

    return {personaForm};
  },
  methods: {},
});
</script>

<template>
  <div class="mb-3 p-3">
    <select id="selectChatModel" aria-label="Chat Model" class="form-select my-2">
      <option selected value="gpt-4">GPT 4</option>
      <option value="gpt-3.5-turbo">GPT 3.5 Turbo</option>
    </select>

    <label class="form-label my-2" for="rangeTemperature">Temperature</label>

    <div class="container">
      <div class="row">
        <div class="col-10 p-0">
          <input id="rangeTemperature" class="form-range" max="1.0" min="0.0" step="0.1" type="range">
        </div>
        <div class="col-2">
          <label>0.7</label>
        </div>
      </div>
    </div>

    <label class="form-label my-2" for="rangeTopP">TopP</label>

    <div class="container">
      <div class="row">
        <div class="col-10 p-0">
          <input id="rangeTopP" class="form-range" max="1.0" min="0.0" step="0.1" type="range">
        </div>
        <div class="col-2">
          <label>0.7</label>
        </div>
      </div>
    </div>

  </div>

</template>

<style scoped>
.placeholder-image {
  font-size: 7em;
  width: 200px;
  height: 200px;
}

.thumbnail-image {
  width: 200px;
  height: 200px;
}

</style>
