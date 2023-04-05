<template>
    <div class="p-3 full-height vertical-scrollbar shadow">

        <HistoryToolbar></HistoryToolbar>

        <div class="list-group list-group-flush border-bottom" id="history-container">

        </div>

    </div>
</template>

<script>
import HistoryToolbar from './HistoryToolbar.vue';

export default {
  name: 'History',
    components: {HistoryToolbar},
};
</script>

<style scoped>

</style>