<template>
    <Collapse :open="open" @update:open="o => $emit('update:open', o)">
        <template v-for="(_, slot) in $slots" :key="slot" #[slot]="data">
            <slot :name="slot" v-bind="getData(data)" :key="slot"></slot>
        </template>
    </Collapse>
</template>

<script lang="ts" setup>
import Collapse from "@components/collapse/Collapse.vue";

interface Props {
    required?: boolean;
    open?: boolean;
}
interface Emits {
    (e: 'update:open', open: boolean): void;
}

const props = withDefaults(defineProps<Props>(),  {
    required: true,
    open: true
});
defineEmits<Emits>();

const getData = (data: any) => ({...data, required: props.required && props.open});
</script>
