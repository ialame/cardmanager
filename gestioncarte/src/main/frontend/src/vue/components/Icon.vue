<template>
    <div class="icon">
        <InlineSvg :src="source" />
    </div>
</template>

<script lang="ts" setup>
import {computed} from "vue";
import InlineSvg from "vue-inline-svg";

interface Props {
    name?: string;
    src?: string;
}

const props = defineProps<Props>();

const source = computed(() => {
    if (props.src) {
        return props.src;
    } else if (props.name) {
        return '/svg/ionicons/' + props.name + '.svg';
    }
    return '';
});

</script>

<style lang="scss" scoped>
div.icon {
    display: inline-block;
    contain: strict;
    fill: currentColor;
    box-sizing: content-box !important;
    &:not(.icon-12):not(.icon-14):not(.icon-16):not(.icon-24):not(.icon-32) {
        width: 16px;
        height: 16px;
    }
}

svg {
    display: block;
    height: 100%;
    width: 100%;
    &:deep {
        fill: currentColor;
        stroke: currentColor;
    }
}
</style>
