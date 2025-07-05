<template>
    <div v-show="opened" class="p-0">
        <slot />
    </div>
</template>

<script lang="ts" setup>
import {useProvideAdvancedFormTab} from "@components/form/advanced/tab/logic";

interface Props {
    tab: string;
}

const props = defineProps<Props>();

const { opened } = useProvideAdvancedFormTab(() => props.tab);
</script>
