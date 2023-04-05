<template>
    <div class="p-3 full-height vertical-scrollbar shadow">

        <HistoryToolbar></HistoryToolbar>

        <div class="list-group list-group-flush border-bottom" id="history-container">

        </div>

    </div>
</template>

<script>
import Toolbar from './Toolbar.vue';

export default {
  name: 'History',
    components: {Toolbar},
};
</script>

<style scoped>

</style>