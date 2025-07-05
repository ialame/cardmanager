<template>
    <FormButton color="secondary" :class="classes" @click="doScroll">
        <Icon name="arrow-up-outline" class="icon-32" />
    </FormButton>
</template>

<script lang="ts" setup>
import {computed} from 'vue';
import FormButton from '@components/form/FormButton.vue';
import Icon from "@components/Icon.vue";
import {useContentDiv} from "@components/side";
import {useScroll} from "@vueuse/core";

const contentDiv = useContentDiv();
const { x, y, arrivedState } = useScroll(contentDiv, { behavior: 'smooth' });
const classes = computed(() => "round-btn shadow-lg pt-2 ms-2 mb-2 scroll-to-top" + (!arrivedState.top ? " show" : ""));
const doScroll = () => {
    x.value = 0;
    y.value = 0;
}
</script>

<style lang="scss">
.scroll-to-top {
	transition: transform 0.25s ease;
}
.scroll-to-top:not(.show) {
	transform: translateX(200%);
}
</style>
