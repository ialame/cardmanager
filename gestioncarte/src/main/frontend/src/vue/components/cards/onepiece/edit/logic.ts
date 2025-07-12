import {OnePieceSetDTO} from "@/types";
import {onePieceSetService} from "@components/cards/onepiece/set";

export const setMapper = {
    get: async (setIds?: string[]) => {
        if (setIds && setIds.length > 0) {
            return await onePieceSetService.get(setIds[0]);
        }
        return undefined;
    },
    set: async (set?: OnePieceSetDTO) => {
        if (set?.id) {
            return [set.id];
        }
        return [];
    }
}
