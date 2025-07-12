import {PokemonCardTranslatorDTO, Service} from "@/types";


export const pokemonCardTranslatorService = new Service<PokemonCardTranslatorDTO, number>('/api/cards/pokemon/translators', t => t?.id);
