import {ExtractedLorcanaCardDTO, Service} from "@/types";
import rest from "@/rest";

export const extractedLorcanaCardsService = new class extends Service<ExtractedLorcanaCardDTO, string> {
    constructor() {
        super('/api/cards/lorcana/extracted');
    }

    async clear(): Promise<void> {
        await rest.delete('/api/cards/lorcana/extracted');
        this.fetch();
    }

    async addCards(cards: ExtractedLorcanaCardDTO[]): Promise<void> {
        await rest.post('/api/cards/lorcana/extracted/bulk', {data: cards});
        this.fetch();
        await this.flush();
    }
    public fetch() {
        super.fetch();
    }
}
