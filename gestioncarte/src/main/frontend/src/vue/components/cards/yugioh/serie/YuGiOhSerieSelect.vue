<template>
    <component v-bind="$attrs" :is="component" :values="series" label="Serie" #default="{ value: serie }">
        <YuGiOhSerieLabel v-if="!isNil(serie) && !isEmpty(serie)" :serie="serie" />
        <span v-else-if="hasAllOption" class="default-option">Toutes</span>
        <span v-else class="default-option" />
    </component>
</template>

<script lang="ts" setup>
import {YuGiOhSerieDTO} from "@/types";
import {computed} from "vue";
import {AdvancedFormSelect} from "@components/form/advanced";
import FormSelect from "@components/form/FormSelect.vue";
import {computedAsync} from "@vueuse/core";
import {isEmpty, isNil} from "lodash";
import YuGiOhSerieLabel from "@components/cards/yugioh/serie/YuGiOhSerieLabel.vue";
import {yugiohSerieService} from "@components/cards/yugioh/serie/service";

interface Props {
    values?: YuGiOhSerieDTO[];
    advanced?: boolean;
    hasAllOption?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
    values: () => [],
    advanced: false,
    hasAllOption: false,
});

const series = computedAsync<YuGiOhSerieDTO[]>(() => !isEmpty(props.values) ? props.values : yugiohSerieService.all.value, []);
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
