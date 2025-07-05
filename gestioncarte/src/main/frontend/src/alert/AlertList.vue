<template>
    <div class="alert-container container position-fixed start-50 translate-middle-x">
        <Alert v-for="(a, i) in alerts" :key="i" v-bind="a" @close="remove(alerts, a)" />
    </div>
</template>

<script lang="ts" setup>
import {useAlerts} from "@/alert/logic";
import Alert from "@/alert/Alert.vue";
import {remove} from "lodash";

const alerts = useAlerts();
</script>

<style lang="scss" scoped>
.alert-container {
    pointer-events: none;
    z-index: 1150;
    :deep(.alert) {
        pointer-events: auto;
    }
}
</style>
