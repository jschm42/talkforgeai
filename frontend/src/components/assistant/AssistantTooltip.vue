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

<script lang="ts">
import {defineComponent} from 'vue';

export default defineComponent({
  name: 'AssistantTooltip',
});
</script>

<template>
  <div class="tooltip-content">
    <slot></slot>
  </div>
</template>

<style scoped>
.tooltip-content {
  position: absolute;
  bottom: -20px; /* Adjust as needed */
  left: 20%; /* Center the tooltip */
  transform: translateX(-15%);
  color: white;
  padding: 5px;
  border-radius: 5px;
  white-space: pre-line; /* To support multiple lines */
  overflow: hidden; /* Hides the text that overflows the card width */
  text-overflow: ellipsis; /* Shows an ellipsis when the text overflows */
  background-color: rgba(0, 0, 0, 0.5);
  max-height: 60%;
  width: 90%
}

</style>