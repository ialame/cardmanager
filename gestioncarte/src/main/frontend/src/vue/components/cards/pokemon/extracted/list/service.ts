import {ExtractedPokemonCardDTO, Service} from "@/types";
import rest from "@/rest";

export const extractedPokemonCardsService = new class extends Service<ExtractedPokemonCardDTO, string> {
    constructor() {
        super('/api/cards/pokemon/extracted');
    }

    async clear(): Promise<void> {
        await rest.delete('/api/cards/pokemon/extracted');
        this.fetch();
    }

    async setCards(cards: ExtractedPokemonCardDTO[]): Promise<void> {
        await rest.put('/api/cards/pokemon/extracted/bulk', {data: cards});
        this.fetch();
        await this.flush();
    }

    async addCards(cards: ExtractedPokemonCardDTO[]): Promise<void> {
        await rest.post('/api/cards/pokemon/extracted/bulk', {data: cards});
        this.fetch();
        await this.flush();
    }

    public fetch() {
        super.fetch();
    }
}
