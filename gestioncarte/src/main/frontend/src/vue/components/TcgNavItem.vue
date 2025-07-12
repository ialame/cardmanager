<template>
    <li class="tcg-nav-item nav-item text-white" :class="{ 'selected': selected }" @click="currentTcg = tcg">
        <Icon v-if="tcg" :src="`/svg/tcg/${tcg}.svg`" class="me-1 icon-16" /><span v-if="title" class="fw-bolder">{{title}}</span>
    </li>
</template>

<script lang="ts" setup>
import Icon from "@components/Icon.vue";
import {useSideBarTabs} from "@/vue/composables/PageComposables";
import {computed} from "vue";

interface Props {
    tcg: string;
    title?: string;
}

const props = defineProps<Props>();

const currentTcg = useSideBarTabs();

const selected = computed(() => props.tcg === currentTcg.value);
</script>

<style lang="scss" scoped>
@import 'src/variables.scss';

li.tcg-nav-item {
    height: 100%;
    transition: 0.3s;
    padding: 16px 12px;
    cursor: pointer;
    &.selected {
        background-color: $primary;
    }
    &:hover:not(.selected) {
        background-color: $gray-500;
    }
}
</style>
