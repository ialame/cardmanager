<template>
	<FormButton v-if="activeTabs.includes(tab)" color="link" class="tab-btn no-focus" :class="{opened: openedTab === tab}" @click="setTab(tab)">
        <div><slot /></div>
    </FormButton>
</template>

<script lang="ts" setup>
import FormButton from '@components/form/FormButton.vue';
import {computed, defineProps} from 'vue';
import {useAdvancedFormTabControl} from "@components/form/advanced/tab/logic";

interface Props {
    tab: string;
}

defineProps<Props>();

const tabControl = useAdvancedFormTabControl();

const activeTabs = computed(() => tabControl?.activeTabs.value ?? []);
const openedTab = computed(() => tabControl?.openedTab.value ?? '');
const setTab = (tab: string) => tabControl?.setTab(tab);
</script>

<style lang="scss" scoped>
@import "src/variables";

button.tab-btn {
	padding: 0;
    margin: 0.375rem 0.3rem;
	transition: transform .2s;

    &:not(.no-scale) {
        &:hover, &.opened {
            transform: scale(1.5);
        }
    }
    &.opened > div {
        border-bottom: 2px solid $primary;
        border-radius: 0;
        padding-bottom: 2px;
    }
}
</style>
