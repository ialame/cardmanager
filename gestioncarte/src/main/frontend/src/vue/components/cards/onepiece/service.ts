import {OnePieceCardDTO, Service} from "@/types";
import rest from "@/rest";

export const onePieceCardService = new class extends Service<OnePieceCardDTO, string> {

    constructor() {
        super('/api/cards/onepiece');
    }

    public async findWithPromo(id: string): Promise<OnePieceCardDTO> {
        return (await rest.get(`/api/cards/onepiece/promos/${id}/card`)) as OnePieceCardDTO;
    }
}
