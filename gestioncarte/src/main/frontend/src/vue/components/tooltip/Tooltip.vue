<template>
 <div class="r-tooltip" v-if="hover">
     <div class="r-tooltip-inner"><slot /></div>
 </div>
</template>

<script lang="ts" setup>
import {getCurrentInstance, onMounted, ref, RendererNode,} from "vue";
import {useElementHover} from "@vueuse/core";

interface Props {
    type?: 'parent' | 'previous' | 'next';
}

const props = withDefaults(defineProps<Props>(), {
    type: 'parent'
});

const instance = getCurrentInstance();
const element = ref();

const hover = useElementHover(element);

const getElement = (el: RendererNode) => {
    switch (props.type) {
        case 'parent':
            return el.parentElement;
        case 'previous':
            return el.previousElementSibling;
        case 'next':
            return el.nextElementSibling;
        default:
            return el;
    }
}

onMounted(() => {
    const el = instance?.vnode.el;

    if (!el) {
        return;
    }
    let parent = getElement(el);

    if (!parent) {
        return;
    }
    if (parent.tagName === "TD") {
        parent = parent.parentElement;
    }
    element.value = parent;
});
</script>
