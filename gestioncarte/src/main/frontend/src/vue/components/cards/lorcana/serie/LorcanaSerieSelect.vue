<template>
    <component v-bind="$attrs" :is="component" :values="series" label="Serie" #default="{ value: serie }">
        <LorcanaSerieLabel v-if="!isNil(serie) && !isEmpty(serie)" :serie="serie" />
        <span v-else-if="hasAllOption" class="default-option">Toutes</span>
        <span v-else class="default-option" />
    </component>
</template>

<script lang="ts" setup>
import {LorcanaSerieDTO} from "@/types";
import {computed} from "vue";
import {AdvancedFormSelect} from "@components/form/advanced";
import FormSelect from "@components/form/FormSelect.vue";
import {computedAsync} from "@vueuse/core";
import {isEmpty, isNil} from "lodash";
import LorcanaSerieLabel from "@components/cards/lorcana/serie/LorcanaSerieLabel.vue";
import {lorcanaSerieService} from "@components/cards/lorcana/serie/service";

interface Props {
    values?: LorcanaSerieDTO[];
    advanced?: boolean;
    hasAllOption?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
    values: () => [],
    advanced: false,
    hasAllOption: false,
});

const series = computedAsync<LorcanaSerieDTO[]>(() => !isEmpty(props.values) ? props.values : lorcanaSerieService.all.value, []);
const component = computed(() => {
    if (props.advanced) {
        return AdvancedFormSelect;
    }
    return FormSelect;
});
</script>

<style lang="scss" scoped>
:deep(div):has(>span.default-option) {
    height: 34px;
}
</style>
