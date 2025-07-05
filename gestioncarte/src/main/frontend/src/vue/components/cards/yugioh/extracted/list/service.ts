import {ExtractedYuGiOhCardDTO, Service} from "@/types";
import rest from "@/rest";

export const extractedYuGiOhCardsService = new class extends Service<ExtractedYuGiOhCardDTO, string> {
    constructor() {
        super('/api/cards/yugioh/extracted');
    }

    async clear(): Promise<void> {
        await rest.delete('/api/cards/yugioh/extracted');
        this.fetch();
    }

    async addCards(cards: ExtractedYuGiOhCardDTO[]): Promise<void> {
        await rest.post('/api/cards/yugioh/extracted/bulk', {data: cards});
        this.fetch();
        await this.flush();
    }
    public fetch() {
        super.fetch();
    }
}
