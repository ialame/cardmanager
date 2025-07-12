<template>
	<component :is="component" v-bind="componentProps" class="dropdown-item"><slot>{{label}}</slot></component>
</template>

<style lang="scss" scoped>
.dropdown-item { cursor: pointer; }
</style>

<script lang="ts" setup>
import {computed} from "vue"
import {RouterLink} from "vue-router";

interface Props {
    label?: string;
    href?: string;
    to?: string;
}

const props = withDefaults(defineProps<Props>(), {
    label: ''
});

const component = computed<'a' | 'div' | typeof RouterLink>(() => {
    if (props.href) {
        return 'a';
    } else if (props.to) {
        return RouterLink;
    }
    return 'div';
});

const componentProps = computed(() => {
    if (props.href) {
        return {href: props.href};
    } else if (props.to) {
        return {to: props.to};
    }
    return {};
});

</script>
