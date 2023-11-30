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

<template>
  <div v-if="isOpen" class="modal" role="dialog" tabindex="-1">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">{{ title }}</h5>
        </div>
        <div class="modal-body">
          <div class="mb-3 modal-message">
            {{ message }}
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-danger my-2" type="button" @click.prevent="onYes">Yes</button>
          <button class="btn btn-secondary my-2" type="button" @click.prevent="onNo">No</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'QuestionModal',
  props: {
    isOpen: {
      type: Boolean,
      default: false,
    },
    title: {
      type: String,
      default: 'Question',
    },
    message: {
      type: String,
      default: 'Are you sure?',
    },
  },
  methods: {
    onYes() {
      this.$emit('answer', true);
    },
    onNo() {
      this.$emit('answer', false);
    },
  },
};
</script>

<style scoped>
.modal-message {
  color: white;
}

h5 {
  color: white;
}

.modal {
  display: block;
  background-color: rgba(0, 0, 0, 0.5);
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1050;
  overflow: auto;
}

</style>
