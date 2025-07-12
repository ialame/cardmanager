<template>
    <Column class="p-0 mt-2">
        <div class="d-flex flex-row">
            <h5 class="ms-2 me-2">{{label}} <IdTooltip :id="id" /></h5>
            <slot :side="side" />
        </div>
    </Column>
    <hr class="mt-0">
</template>
<script lang="ts" setup>

import Column from "@components/grid/Column.vue";
import {computed} from "vue";
import IdTooltip from "@components/tooltip/IdTooltip.vue";
import {useAdvancedFormSide} from "@components/form/advanced/merge/logic";
import {useAdvancedFormContext} from "@components/form/advanced/logic";

interface Props {
    labels?: string | [string] | [string, string];
}

const props = withDefaults(defineProps<Props>(), {
    labels: () => ["Donnée extraite", "Donnée en BDD"]
});
const side = useAdvancedFormSide();
const { data, mergeSources } = useAdvancedFormContext<any>();
const id = computed(() =>  {
    if (side.value === -1) {
        return data.value.id;
    }
    return mergeSources.value[side.value].id;
});
const label = computed(() => {
    if (typeof props.labels === 'string') {
        return props.labels;
    } else if (side.value === -1 || props.labels.length === 1) {
        return props.labels[0];
    }
    return props.labels[1];
});
</script>
