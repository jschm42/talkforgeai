<template>
    <div class="input-group">
        <div class="input-group-text" id="new-session-button"><i class="bi bi-x-circle"
                                                                 style="font-size: 2em"></i></div>
        <textarea class="form-control shadow" id="input_area" placeholder="Enter text..." rows="5"></textarea>
        <div class="input-group-text" id="submit-button"><i class="bi bi-send-check-fill"
                                                            style="font-size: 2em"></i></div>
    </div>
</template>

<script>
export default {
  name: 'Input',
};
</script>

<style scoped>

</style>