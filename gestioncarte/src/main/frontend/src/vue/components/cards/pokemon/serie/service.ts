import {PokemonSerieDTO, Service} from "@/types";

export const pokemonSerieService = new Service<PokemonSerieDTO, string>('/api/cards/pokemon/series');
