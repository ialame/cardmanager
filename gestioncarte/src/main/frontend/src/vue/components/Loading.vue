<template>
    <slot v-if="ready"></slot>
    <div v-else class="d-flex justify-content-center mt-4">
        <div class="spinner-border loading-spinner" role="status"></div>
    </div>
</template>

<script lang="ts" setup>
interface Props {
    ready?: boolean;
}

withDefaults(defineProps<Props>(), {
    ready: true
});

</script>

<style lang="scss" scoped>
div.spinner-border.loading-spinner {
    @import "src/variables";

    width: 10rem;
    height: 10rem;
    border-top-color: $primary;
    border-left-color: $primary;
    border-right-color: $light-bg;
    border-bottom-color: $primary;
}

</style>
