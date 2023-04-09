<template>
  <div class="p-3 full-height vertical-scrollbar shadow">

    <Toolbar></Toolbar>

    <div class="list-group list-group-flush border-bottom">

      <div v-for="entry in indexState.entries" :key="entry.sessionId">
        <a id='{{entry.sessionId}}' class="list-group-item list-group-item-action py-3 lh-sm {{entry.active}}">
          <div class="d-flex w-100 align-items-center justify-content-between">
            <strong class="mb-1 text-truncate" title="{{entry.title}}">{{ entry.title }}</strong>
          </div>
          <!--                    <div class="col-10 mb-1 small">{{ entry.timestamp }}</div>-->
        </a>
      </div>

    </div>

  </div>
</template>

<script>
import Toolbar from './Toolbar.vue';
import Store from '../../store/store';

export default {
  name: 'History',
  components: {Toolbar},
  data() {
    return {
      indexState: Store.state.index,
    };
  },
  methods: {
    load() {
      Store.mutations.index.load();
    },
  },
  mounted() {
    Store.mutations.index.load();
  },
  changed() {
    console.log('History-Component changed');
  },
};
</script>

<style scoped>

</style>
