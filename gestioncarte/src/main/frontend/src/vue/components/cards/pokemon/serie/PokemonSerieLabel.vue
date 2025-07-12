<template>
  <Flag v-if="localization" :langue="localization" /> {{ name }} <IdTooltip :id="computedSerie?.id"/>
</template>

<script lang="ts" setup>
import IdTooltip from "@components/tooltip/IdTooltip.vue";
import {PokemonSerieDTO, PokemonSerieTranslationDTO} from "@/types";
import {computed} from "vue";
import {Flag, getTranslationField, getTranslationName, LocalizationCode} from "@/localization";
import {computedAsync} from "@vueuse/core";
import {isString} from "lodash";
import {pokemonSerieService} from "@components/cards/pokemon/serie/service";

interface Props {
  serie?: PokemonSerieDTO;// | string;
  langue: LocalizationCode;
  lang: PokemonSerieTranslationDTO | undefined;
}

const props = defineProps<Props>();

// const computedSerie = computedAsync(() => isString(props.serie) ? pokemonSerieService.get(props.serie) : props.serie);
// const name = computed(() => getTranslationName("SERIE", computedSerie.value?.translations));
// const localization = computed(() => getTranslationField<PokemonSerieTranslationDTO>(computedSerie.value?.translations)?.[0]);

const computedSerie = props.serie;
const name = computed(() =>  props.serie?.translations?.[props.langue]?.name);
//const localization = computed(() => props.serie?.translations?.[props.langue]);
const localization = computed(() => getTranslationField<PokemonSerieTranslationDTO>(computedSerie?.translations)?.[0]);
</script>
