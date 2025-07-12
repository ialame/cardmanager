<template>
    <template v-if="activeTabs.length > 1">
        <div v-bind="$attrs" class="d-flex justify-content-between mt-3">
            <FormButton color="link" @click="goToPreviousTab()" class="advanced-form-tab-btn no-focus"><Icon name="chevron-back" class="icon-32" /></FormButton>
            <div class="d-flex justify-content-center w-100">
                <slot></slot>
            </div>
            <FormButton color="link" @click="goToNextTab()" class="advanced-form-tab-btn no-focus"><Icon name="chevron-forward" class="icon-32" /></FormButton>
        </div>
        <hr />
    </template>
</template>

<script lang="ts" setup>
import FormButton from '@components/form/FormButton.vue';
import {computed, onMounted} from 'vue';
import Icon from "@components/Icon.vue";
import {useAdvancedFormTabControl} from "@components/form/advanced/tab/logic";

interface Props {
    disableKeys?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
    disableKeys: false,
});

const tabControl = useAdvancedFormTabControl();

const goToNextTab = () => tabControl?.goToNextTab();
const goToPreviousTab = () => tabControl?.goToPreviousTab();
const activeTabs = computed(() => tabControl?.activeTabs.value ?? []);

onMounted(() => window.addEventListener('keydown', (e: KeyboardEvent) => {
    const activeElement = document.activeElement;

    if (props.disableKeys || activeElement?.tagName === 'INPUT' || activeElement?.tagName === 'TEXTAREA') {
        return;
    }
    if (e.key === 'ArrowLeft') {
        e.preventDefault();
        goToPreviousTab();
    } else if (e.key === 'ArrowRight') {
        e.preventDefault();
        goToNextTab();
    }
}));
defineExpose({ goToNextTab, goToPreviousTab });
</script>
