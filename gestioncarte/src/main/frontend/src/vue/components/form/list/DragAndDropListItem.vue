<template>
    <div ref="target" :class="{'dragging': isDragging}" :style="style">
        <slot></slot>
    </div>
</template>

<script lang="ts" setup>

import {computed, defineEmits, defineExpose, ref} from "vue";
import {Position, useDraggable} from "@vueuse/core";

interface Emits {
    (e: 'startDragging'): void;
    (e: 'drop'): void;
}

const emit = defineEmits<Emits>();

const target = ref();
const startPos = ref<Position>({x: 0, y: 0});
const {x, y, isDragging} = useDraggable(target, {
    onStart: (_p: Position, event: PointerEvent) => {
        if (['INPUT', 'TEXTAREA', 'A', 'BUTTON', 'SELECT', 'OPTION'].includes((event.target as HTMLElement).tagName)) {
            return false;
        }

        const rect = target.value!.getBoundingClientRect();

        startPos.value = {x: rect.left || 0, y: rect.top || 0};
        emit('startDragging');
    },
    onEnd: () => emit('drop'),
});
const style = computed(() => `touch-action:none;left:${x.value - startPos.value.x}px;top:${y.value - startPos.value.y}px;`);

defineExpose({isDragging});
</script>

<style lang="scss" scoped>
.dragging {
    position: relative !important;
    z-index: 1000;
}
</style>
