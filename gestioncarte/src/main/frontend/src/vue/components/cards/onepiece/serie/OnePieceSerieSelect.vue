<template>
    <component v-bind="$attrs" :is="component" :values="series" label="Serie" #default="{ value: serie }">
        <OnePieceSerieLabel v-if="!isNil(serie) && !isEmpty(serie)" :serie="serie" />
        <span v-else-if="hasAllOption" class="default-option">Toutes</span>
        <span v-else class="default-option" />
    </component>
</template>

<script lang="ts" setup>
import {OnePieceSerieDTO} from "@/types";
import {computed} from "vue";
import {AdvancedFormSelect} from "@components/form/advanced";
import FormSelect from "@components/form/FormSelect.vue";
import {computedAsync} from "@vueuse/core";
import {isEmpty, isNil} from "lodash";
import OnePieceSerieLabel from "@components/cards/onepiece/serie/OnePieceSerieLabel.vue";
import {onePieceSerieService} from "@components/cards/onepiece/serie";

interface Props {
    values?: OnePieceSerieDTO[];
    advanced?: boolean;
    hasAllOption?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
    values: () => [],
    advanced: false,
    hasAllOption: false,
});

const series = computedAsync<OnePieceSerieDTO[]>(() => !isEmpty(props.values) ? props.values : onePieceSerieService.all.value, []);
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
