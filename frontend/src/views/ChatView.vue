<template>
  <div class="p-2">
    <div class="row">

    </div>

    <div class="row g-1 full-height">
      <!-- History Column -->
      <div class="col-3">
        <ChatHeader></ChatHeader>
        <ChatHistory></ChatHistory>
      </div>
      <!-- End History Column -->

      <div class="col-9">
        <ChatContainer></ChatContainer>
      </div>

      <!-- Chat Column -->
      <!--      <div class="col-9 gx-3 full-height">-->
      <!--        <div class="d-flex flex-column full-height">-->

      <!--          <ChatContainer></ChatContainer>-->
      <!--        </div>-->
      <!--      </div>-->
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent} from 'vue';
import ChatContainer from '@/components/ChatContainer.vue';
import ChatHistory from '@/components/history/ChatHistory.vue';
import ChatHeader from '@/components/ChatHeader.vue';
import {useChatStore} from '@/store/chat-store';

export default defineComponent({
  components: {ChatHeader, ChatHistory, ChatContainer},
  props: ['personaId'],
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function
    return {store};
  },
  data() {
    return {};
  },
  async mounted() {
    await this.store.selectPersonaById(this.personaId);
  },
});
</script>

<style>
pre {
  background-color: #303030;
  color: white;
  border: 1px solid #e9ecef;
  border-radius: 0.25rem;
  padding: 1rem;
}

.code-word {
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 0.25rem;
  /*font-family: "Source Code Pro", monospace;*/
  font-family: 'Courier New', monospace
}

body, html {
  overflow: hidden;
  height: 100vh;
}

.full-height {
  height: 100vh;
}

.vertical-scrollbar {
  overflow-y: auto;
}

.no-horizontal-scrollbar {
  overflow-x: hidden;
}

.image-prompt-element {
  background-color: bisque;
  border-color: black;
  border-radius: 5px;
  box-shadow: 3px 3px 2px 1px rgba(50, 50, 50, .2);
  min-width: 110px;
  min-height: 23px;
  padding: 3px;
}

</style>

