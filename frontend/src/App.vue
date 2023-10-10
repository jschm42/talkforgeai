<template>
  <div style="border-color: red; border-style: solid; border-width: 1px; color: white">
    <!--    <tf-latex latex="S = \sum_{{n=1}}^{\infty} \frac{{1}}{{n^2}}"></tf-latex>-->

    <tf-latex latex="\begin{equation}
\int_a^b f(x) dx
\end{equation}"></tf-latex>
  </div>


  <router-view/>
</template>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
</style>


<script>
import {useChatStore} from '@/store/chat-store';

export default {
  name: 'ChatContainer',
  setup() {
    const store = useChatStore(); // Call useMyStore() inside the setup function

    return {
      store,
    };
  },
  data() {
    return {};
  },
  mounted() {
  },
};
</script>
