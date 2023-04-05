<script>
export default {
  data() {
    return {
      count: 0,
    };
  },
};

</script>

<style>
body {
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica,
    Arial, sans-serif;
    margin: auto;
    max-width: 38rem;
    padding: 2rem;
}
</style>

<template>
    <button @click="count++">
        You clicked me {{ count }} times.
    </button>
</template>
