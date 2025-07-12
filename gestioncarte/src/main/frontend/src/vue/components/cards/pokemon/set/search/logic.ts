import {computed} from "vue";
import {PokemonSerieDTO, PokemonSetDTO, Predicate} from "@/types";
import {MaybeRefOrGetter, toValue} from "@vueuse/core";
import {isEmpty} from "lodash";
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";
import {pokemonSerieService} from "@components/cards/pokemon/serie";
import pokemonSetService = PokemonComposables.pokemonSetService;

export const useDisplaySeries = (predicate?: Predicate<PokemonSerieDTO>, series?: MaybeRefOrGetter<PokemonSerieDTO[]>) => {
    const allSeries = pokemonSerieService.all;
    const computedSeries = computed(() => series === undefined || isEmpty(toValue(series)) ? allSeries.value : toValue(series));

    return computed<PokemonSerieDTO[]>(() => computedSeries.value.filter(s => predicate ? predicate(s) : true));
}

export const useDisplaySets = (predicate?: (s: PokemonSetDTO) => boolean, sets?: MaybeRefOrGetter<PokemonSetDTO[]>) => {
    const allSets = pokemonSetService.all;
    const computedSets = computed(() => sets === undefined || isEmpty(toValue(sets)) ? allSets.value : toValue(sets));

    return computed<PokemonSetDTO[]>(() => computedSets.value.filter(s => predicate ? predicate(s) : true));
}
