<template>
    <div v-show="opened" class="p-0">
        <slot />
    </div>
</template>

<script lang="ts" setup>
import {computed} from "vue";
import {useAdvancedFormTabControl} from "@components/form/advanced/tab/logic";


interface Props {
    tabs: string | string[] | ((s: string) => boolean);
}

const props = defineProps<Props>();

const tabControl = useAdvancedFormTabControl();

const isOpened = (t: string) => {
    if (!tabControl) {
        return true;
    }

    const activeTabs = tabControl.activeTabs.value || [];

    if (t === '_first') {
        return activeTabs[0] === tabControl.openedTab.value;
    } else if (t === '_last') {
        return activeTabs[activeTabs.length - 1] === tabControl.openedTab.value;
    }
    return tabControl.openedTab.value === t;
};
const opened = computed(() => {
    if (typeof props.tabs === 'function') {
        return props.tabs(tabControl?.openedTab.value || '');
    } else if (typeof props.tabs === 'string') {
        return isOpened(props.tabs);
    }
    return props.tabs.some(isOpened);
});
</script>
