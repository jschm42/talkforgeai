<!--
  - Copyright (c) 2023 Jean Schmitz.
  -
  - Licensed under the Apache License, Version 2.0 (the "License");
  - you may not use this file except in compliance with the License.
  - You may obtain a copy of the License at
  -
  -     http://www.apache.org/licenses/LICENSE-2.0
  -
  - Unless required by applicable law or agreed to in writing, software
  - distributed under the License is distributed on an "AS IS" BASIS,
  - WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  - See the License for the specific language governing permissions and
  - limitations under the License.
  -->

<script>
import {defineComponent} from 'vue';
import {useAppStore} from '@/store/app-store';

export default defineComponent({
  name: 'ErrorAlerts',
  data: () => ({}),
  setup() {
    const store = useAppStore(); // Call useMyStore() inside the setup function
    return {store};
  },
  computed: {
    errors() {
      return this.store.errors;
    },
    hasErrors() {
      return this.store.hasErrors();
    },
  },
  methods: {
    clearError(index) {
      console.log('clearError vorher', index, this.errors);
      this.errors.splice(index, 1);
      console.log('clearError nachher', index, this.errors);
    },
  },
});
</script>

<template>
  <div v-if="hasErrors" class="fixed-bottom-container">
    <div v-for="(error, index) in store.errors" :key="error.id"
         class="alert alert-danger d-flex align-items-center"
         role="alert">
      <svg aria-label="Warning:" class="bi bi-exclamation-triangle-fill flex-shrink-0 me-2"
           fill="currentColor" height="24"
           role="img" viewBox="0 0 16 16" width="24"
           xmlns="http://www.w3.org/2000/svg">
        <path
            d="M8.982 1.566a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767L8.982 1.566zM8 5c.535 0 .954.462.9.995l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 5.995A.905.905 0 0 1 8 5zm.002 6a1 1 0 1 1 0 2 1 1 0 0 1 0-2z"/>
      </svg>
      {{ error.message }}
      <button aria-label="Close" class="btn-close ms-auto" data-bs-dismiss="alert"
              type="button"
              @click="clearError(index)"></button>
    </div>
  </div>
</template>

<style scoped>
.fixed-bottom-container {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 1030; /* High z-index to ensure it floats above other content */
}

.alert {
  margin-left: 5px;
  margin-right: 5px;
  color: white;
  background-color: rgba(200, 0, 0, 0.7);
}
</style>