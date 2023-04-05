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

</style>

<template>
    <button @click="count++">
        You clicked me {{ count }} times.
    </button>
</template>
