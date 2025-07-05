<template>
    <component v-bind="$attrs" :is="component" :values="sets" :label="label" #default="{ value: set }">
        <PokemonSetLabel v-if="!isNil(set) && !isEmpty(set)" :set="set" :langue="langue"/>
        <span v-else-if="hasAllOption" class="default-option">Toutes</span>
        <span v-else class="default-option" />
    </component>
</template>

<script lang="ts" setup>
import {PokemonSetDTO} from "@/types";

import {computed, DefineComponent} from "vue";
import {AdvancedFormSelect} from "@components/form/advanced";
import FormSelect from "@components/form/FormSelect.vue";
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";
import {computedAsync} from "@vueuse/core";
import {isEmpty, isNil} from "lodash";
import PokemonSetLabel from "@components/cards/pokemon/set/PokemonSetLabel.vue";
import pokemonSetService = PokemonComposables.pokemonSetService;
import {LocalizationCode} from "@/localization";

interface Props {
    values?: PokemonSetDTO[];
    advanced?: boolean;
    hasAllOption?: boolean;
    label?: string;
    langue: LocalizationCode;
}

const props = withDefaults(defineProps<Props>(), {
    values: () => [],
    advanced: false,
    hasAllOption: false,
    label: 'Extension'
});

const sets = computedAsync(() => props.values || pokemonSetService.all.value, []);
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
