<template>
    <div class="p-3 full-height vertical-scrollbar shadow">

        <HistoryToolbar></HistoryToolbar>

        <div class="list-group list-group-flush border-bottom">

            <div v-for="(line, index) in index" :key="index">
                <a id='{{line.sessionId}}' class="list-group-item list-group-item-action py-3 lh-sm {{line.active}}">
                    <div class="d-flex w-100 align-items-center justify-content-between">
                        <strong class="mb-1 text-truncate" title="{{line.title}}">{{ line.title }}</strong>
                    </div>
                    <div class="col-10 mb-1 small">{{ line.timestamp }}</div>
                </a>
            </div>

        </div>

    </div>
</template>

<script>
import Toolbar from './Toolbar.vue';

export default {
  name: 'History',
  components: {Toolbar},
  data() {
    return {
      index: [],
    };
  },
  mounted() {
    this.index = window.electronAPI.loadIndex();
  },
};
</script>

<style scoped>

</style>