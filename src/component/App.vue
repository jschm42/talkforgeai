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

/*
image-prompt {
  display: block;
  width: 110px;
  height: 23px;
  background-color: #ddd;
  border: 2px solid #aaa;
  border-radius: 10px;
  overflow: hidden;
  position: relative;
  color: #ddd;
  box-shadow: 5px 3px 3px #aaa;
}

image-prompt::before {
  content: "X";
  --bs-spinner-animation-speed: 0.75s;
  --bs-spinner-animation-name: spinner-grow;
  display: inline-block;
  vertical-align: var(--bs-spinner-vertical-align);
  border-radius: 50%;
  animation: var(--bs-spinner-animation-speed) linear infinite var(--bs-spinner-animation-name);
  position: relative;
  top: -3px;
  left: 5px;
  font-size: 14px;
  font-weight: bold;
  color: black;
}

 */

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
