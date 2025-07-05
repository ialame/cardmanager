import {PokemonCardDTO, Service} from "@/types";
import rest from "@/rest";

export const pokemonCardService = new class extends Service<PokemonCardDTO, string> {

    constructor() {
        super('/api/cards/pokemon');
    }

    public async findWithPromo(id: string): Promise<PokemonCardDTO> {
        return (await rest.get(`/api/cards/pokemon/promos/${id}/card`)) as PokemonCardDTO;
    }

    // public async findWithPromo(id: string): Promise<null> {
    //     //return (await rest.get(`/api/cards/pokemon/promos/${id}/card`)) as PokemonCardDTO;
    //     return null;
    // }
}
