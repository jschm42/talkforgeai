<template>
    <div class="p-2 m-1 rounded shadow input-container">
        <!-- Tabs -->
        <ul class="nav nav-pills mb-3" id="pills-tab" role="tablist">

            <li class="nav-item" role="presentation">
                <button aria-controls="pills-message" aria-selected="true" class="nav-link active"
                        data-bs-target="#pills-message"
                        data-bs-toggle="pill" id="pills-message-tab" role="tab" type="button">ChatMessage
                </button>
            </li>

            <li class="nav-item" role="presentation">
                <button aria-controls="pills-system" aria-selected="true" class="nav-link"
                        data-bs-target="#pills-system"
                        data-bs-toggle="pill" id="pills-system-tab" role="tab" type="button">System
                </button>
            </li>

            <li class="nav-item" role="presentation">
                <button aria-controls="pills-parameters" aria-selected="false" class="nav-link"
                        data-bs-target="#pills-parameters"
                        data-bs-toggle="pill" id="pills-parameters-tab" role="tab" type="button">Parameters
                </button>
            </li>
        </ul>
        <div class="tab-content" id="pills-tabContent">
            <div aria-labelledby="pills-message-tab" class="tab-pane fade show active" id="pills-message"
                 role="tabpanel" tabindex="0">
                <Input></Input>
            </div>

            <div aria-labelledby="pills-system-tab" class="tab-pane fade" id="pills-system" role="tabpanel"
                 tabindex="0">
                <System></System>
            </div>
            <div aria-labelledby="pills-parameters-tab" class="tab-pane fade" id="pills-parameters" role="tabpanel"
                 tabindex="0">
                <Parameters></Parameters>
            </div>
        </div>
        <!-- Ende Tabs -->
    </div>
</template>

<script>
import Parameters from './Parameters.vue';
import System from './System.vue';
import Input from './Input.vue';

export default {
  name: 'ChatControl',
    components: {Input, Parameters, System},
};
</script>

<style scoped>

</style>