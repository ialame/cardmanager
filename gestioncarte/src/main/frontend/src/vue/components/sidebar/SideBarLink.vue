<template>
    <li class="sidebar-link" :class="{'selected': selected}">
        <RouterLink :to="to">{{label}}</RouterLink>
    </li>
</template>

<script lang="ts" setup>
import {RouterLink, useRoute} from "vue-router";
import {computed} from "vue";

interface Props {
    to?: string;
    label?: string;
}

const props = withDefaults(defineProps<Props>(), {
    to: '',
    label: ''
});

const route = useRoute();
const selected = computed(() => route.fullPath === props.to);
</script>

<style lang="scss" scoped>
@import 'src/variables.scss';


li.sidebar-link {
    &.selected {
        background-color: $primary;
        border-radius: 0.85rem;
        margin-left: auto;

        a {
            color: white;
        }
    }

    &:hover:not(.selected) {
        background-color: $gray-500;
        border-radius: 0.85rem;
        margin-left: auto;
    }
}
</style>
