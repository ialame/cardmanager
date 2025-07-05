<template>
    <slot v-if="exists" />
</template>

<script lang="ts" setup>
import {computed} from 'vue';
import {useAdvancedFormContext} from "@components/form/advanced/logic";
import {isNil} from "lodash";
import {useProvideAdvancedFormSide} from "@components/form/advanced/merge/logic";

interface Props {
    side?: number;
}

const props = withDefaults(defineProps<Props>(), {
    side: -1
});

const {mergeSources} = useAdvancedFormContext();
useProvideAdvancedFormSide(() => props.side);
const exists = computed(() => props.side === -1 || !isNil(mergeSources.value[props.side]));
</script>
