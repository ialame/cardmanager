import {CardTagDTO, Service} from "@/types";
import {LocalizationCode} from "@/localization";

export const cardTagService = new class extends Service<CardTagDTO, string> {
    constructor() {
        super('/api/cards/tags');
    }

    async findAllByTypeAndName(type: string, name: string, localization?: LocalizationCode): Promise<CardTagDTO[]> {
        const lowerName = name.toLowerCase();
        const l: LocalizationCode = localization || 'us';

        return this.find(tag => tag.type === type && !!tag.translations?.[l]?.name?.toLowerCase().includes(lowerName));
    }
}
