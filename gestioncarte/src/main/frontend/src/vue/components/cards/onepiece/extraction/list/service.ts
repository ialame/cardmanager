import {ExtractedOnePieceCardDTO, Service} from "@/types";
import rest from "@/rest";

export const extractedOnePieceCardsService = new class extends Service<ExtractedOnePieceCardDTO, string> {
    constructor() {
        super('/api/cards/onepiece/extracted');
    }

    async clear(): Promise<void> {
        await rest.delete('/api/cards/onepiece/extracted');
        this.fetch();
    }

    async addCards(cards: ExtractedOnePieceCardDTO[]): Promise<void> {
        await rest.post('/api/cards/onepiece/extracted/bulk', {data: cards});
        this.fetch();
        await this.flush();
    }
    public fetch() {
        super.fetch();
    }
}
