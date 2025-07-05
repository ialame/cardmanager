<template>
    <div class="side-buttons position-fixed float-end pt-1 pe-1 d-flex flex-row-reverse z-top" :class="{'offset': hasScroll}">
        <div class="side-buttons d-flex flex-column">
            <slot />
        </div>
	</div>
</template>

<script lang="ts" setup>
import {computed} from 'vue';
import {useContentDiv} from "@components/side/contentDiv";
import {useElementSize, useResizeObserver} from "@vueuse/core";
import {ref} from "vue-demi";

const contentDiv = useContentDiv();
const {height} = useElementSize(contentDiv);
const scrollHeight = ref(0);

useResizeObserver(contentDiv, ([e]) => scrollHeight.value = e.target.scrollHeight);
const hasScroll = computed(() => scrollHeight.value > height.value);

</script>

<style lang="scss" scoped>
.side-buttons {
    pointer-events: none;
    &.offset {
        right: 1rem;
    }
    &:not(.offset) {
        right: 0;
    }
    :deep(button) {
        pointer-events: auto;
    }
}
</style>
