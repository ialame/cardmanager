import {getTranslationField, LocalizationCode, sortLocalizations} from "@/localization";
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";
import {PokemonSetDTO, PokemonSetTranslationDTO} from "@/types";
import pokemonSetService = PokemonComposables.pokemonSetService;

export const getSetLocalizations = async (setIds: string[]): Promise<LocalizationCode[]> => sortLocalizations((await Promise.all(setIds
    .map(id => pokemonSetService.get(id))))
    .flatMap(set => (set ? Object.values(set.translations)
        .filter(t => t.available)
        .map(t => t.localization) : []))
    .filter(l => l !== 'kr' && l !== 'cn' && l !== 'zh'));

export const getSetName = (set?: PokemonSetDTO): string => getTranslationField<PokemonSetTranslationDTO, "name">(set?.translations, "name")?.[1] || '!!! EXTENSION SANS NOM !!!';
