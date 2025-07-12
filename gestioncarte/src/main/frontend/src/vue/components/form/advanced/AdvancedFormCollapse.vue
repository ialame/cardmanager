<template>
    <div class="col form-group advanced-form-collapse" v-bind="columnProps" :data-path="pathToString(path)">
        <slot name="before" />
        <RequiredCollapse v-if="requirement === 'collapsable'" :open="open" @update:open="o => $emit('update:open', o)" :label="label">
            <template v-for="(_, slot) in $slots" :key="slot" #[slot]="data">
                <slot :name="slot" v-bind="data" :key="slot" />
            </template>
        </RequiredCollapse>
        <template v-else>
            <div class="d-flex flex-row">
                <label class="form-label p-0" v-if="hasLabel"><slot name="label" :required="requirement === 'required'">{{label}}</slot></label>
                <slot name="after-label" :required="requirement === 'required'" />
            </div>
            <slot :required="requirement === 'required'" />
        </template>
    </div>
</template>

<script lang="ts" setup>
import {computed, useSlots, watch} from 'vue';
import RequiredCollapse from "@components/collapse/RequiredCollapse.vue";
import {useAdvancedFormCollapse} from "@components/form/advanced/logic";
import {Path, pathToString} from "@/path";
import {useAlignedElement} from "@components/form/advanced/merge/alignment";

interface Props {
    path: Path;
    label?: string;
    open?: boolean;
    alignKey?: string;
}
interface Emits {
    (e: 'update:open', open: boolean): void,
}

const props = withDefaults(defineProps<Props>(),  {
    label: '',
    open: true,
});
const emit = defineEmits<Emits>();
const slots = useSlots();

const columnProps = useAlignedElement(() => props.alignKey ?? pathToString(props.path));
const { requirement, open: advancedFormCollapseOpen } = useAdvancedFormCollapse(() => props.path);

const hasLabel = computed(() => !!props.label || !!slots.label);

watch(advancedFormCollapseOpen, (v, o) => {
    if (v !== o) {
        emit('update:open', v);
    }
});
watch(() => props.open, (v, o) => {
    if (v !== o) {
        advancedFormCollapseOpen.value = v;
    }
});
</script>
