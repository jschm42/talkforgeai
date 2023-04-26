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
import hljs from 'highlight.js';

hljs.highlightAll();

export default {
  name: 'App',
  components: {Chat, History},
};
</script>

<style>
pre {
  background-color: #f8f9fa;
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


.prompt {
  background-color: red;
  width: 100px;
  height: 10px;

}


image-prompt::before {
  content: "Image Prompt";
  position: absolute;
  font-size: 14px;
  font-weight: bold;
  color: red;
  left: 5px;
}

image-prompt {
  background-color: beige;
  border-color: black;
  width: 110px;
  height: 23px;
  overflow: hidden;
  color: beige;
  display: block;
  position: relative;
  border-radius: 5px;
  box-shadow: black;
}


</style>
