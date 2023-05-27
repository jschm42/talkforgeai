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
