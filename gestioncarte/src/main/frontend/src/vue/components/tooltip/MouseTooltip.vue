<template>
 <div class="r-tooltip position-fixed" :style="{'top': y + 'px', 'left': (x + 20) + 'px'}" v-if="show">
     <div class="r-tooltip-inner"><slot /></div>
 </div>
</template>

<script lang="ts" setup>
import {DOMComposables} from "@/vue/composables/DOMComposables";

interface Props {
    show: boolean;
}

withDefaults(defineProps<Props>(), {
    show: false,
});

const { x, y } = DOMComposables.useSharedMouse();

</script>
