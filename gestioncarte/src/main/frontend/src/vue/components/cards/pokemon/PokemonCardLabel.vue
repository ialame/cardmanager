<template>
    <Flag v-if="computedLocalization" class="mt-2 me-1" :lang="computedLocalization" /> {{ name }}{{number ? ` (${number})` : ''}} <IdTooltip :id="computedCard?.id"/>
</template>

<script lang="ts" setup>
import IdTooltip from "@components/tooltip/IdTooltip.vue";
import {PokemonCardDTO, PokemonCardTranslationDTO} from "@/types";
import {computed} from "vue";
import {Flag, getTranslationField, LocalizationCode} from "@/localization";
import {computedAsync} from "@vueuse/core";
import {isString} from "lodash";
import {pokemonCardService} from "@components/cards/pokemon/service";

interface Props {
    card?: PokemonCardDTO | string;
    localization?: LocalizationCode;
}

const props = defineProps<Props>();

const computedCard = computedAsync<PokemonCardDTO>(() => isString(props.card) ? pokemonCardService.get(props.card) : props.card);
const name = computed(() => {
    if (props.localization) {
        const name = computedCard.value?.translations[props.localization]?.name;

        if (name) {
            return name;
        }
    }
    return getTranslationField<PokemonCardTranslationDTO, "name">(computedCard.value?.translations, "name")?.[1] || '!!! CARTE SANS NOM !!!'
});
const number = computed(() => {
    if (props.localization) {
        const number = computedCard.value?.translations[props.localization]?.number;

        if (number) {
            return number;
        }
    }
    return getTranslationField<PokemonCardTranslationDTO, "number">(computedCard.value?.translations, "number")?.[1] || ''
});
const computedLocalization = computed(() => props.localization || getTranslationField<PokemonCardTranslationDTO>(computedCard.value?.translations)?.[0]);
</script>
