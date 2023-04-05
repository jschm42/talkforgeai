<template>
    <div class="row g-1 full-height">
        <!-- History Column -->
        <div class="col-3">
           <history></history>
        </div>
        <!-- End History Column -->

        <!-- Chat Column -->
        <div class="col-9 gx-3 full-height">
            <div class="d-flex flex-column full-height">
                <Chat></Chat>
            </div>
        </div>
    </div>
</template>

<script>
import History from './history/History.vue';
import Chat from './chat/Chat.vue';

export default {
  name: 'App',
    components: {Chat, History},
};
</script>

<style scoped>

</style>