<template>
    <component v-bind="$attrs" :is="component" :values="series" label="Serie" #default="{ value: serie }">
        <PokemonSerieLabel v-if="!isNil(serie) && !isEmpty(serie)" :serie="serie" :langue="langue"/>
        <span v-else-if="hasAllOption" class="default-option">Toutes</span>
        <span v-else class="default-option" />
    </component>
</template>

<script lang="ts" setup>
import {PokemonSerieDTO} from "@/types";
import {computed, DefineComponent} from "vue";
import {AdvancedFormSelect} from "@components/form/advanced";
import FormSelect from "@components/form/FormSelect.vue";
import {computedAsync} from "@vueuse/core";
import {isEmpty, isNil} from "lodash";
import PokemonSerieLabel from "@components/cards/pokemon/serie/PokemonSerieLabel.vue";
import {pokemonSerieService} from "@components/cards/pokemon/serie/service";
import {LocalizationCode} from "@/localization";

interface Props {
    values?: PokemonSerieDTO[];
    advanced?: boolean;
    hasAllOption?: boolean;
    langue: LocalizationCode;
}

const props = withDefaults(defineProps<Props>(), {
    values: () => [],
    advanced: false,
    hasAllOption: false,
});

const series = computedAsync<PokemonSerieDTO[]>(() => !isEmpty(props.values) ? props.values : pokemonSerieService.all.value, []);
const component = computed<DefineComponent<{}, {}, any>>(() => {
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
