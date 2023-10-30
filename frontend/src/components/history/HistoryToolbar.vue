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

  <div aria-label="Toolbar with button groups" class="my-3 btn-toolbar" role="toolbar">
    <div aria-label="First group" class="btn-group me-2" role="group">
      <button class="btn btn-primary" type="button">1</button>
      <button class="btn btn-primary" type="button">4</button>
    </div>
    <div aria-label="Second group" class="btn-group me-2" role="group">
      <button class="btn btn-secondary" type="button">5</button>
      <button class="btn btn-secondary" type="button">7</button>
    </div>
    <div aria-label="Third group" class="btn-group" role="group">
      <button class="btn btn-info" type="button">8</button>
    </div>
  </div>

</template>

<script lang="ts">
export default {
  name: 'HistoryToolbar',
};
</script>

<style scoped>

</style>
