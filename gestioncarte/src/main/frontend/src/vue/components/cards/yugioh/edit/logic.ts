import {YuGiOhSetDTO} from "@/types";
import {yugiohSetService} from "@components/cards/yugioh/set";

export const setMapper = {
    get: async (setIds?: string[]) => {
        if (setIds && setIds.length > 0) {
            return await yugiohSetService.get(setIds[0]);
        }
        return undefined;
    },
    set: async (set?: YuGiOhSetDTO) => {
        if (set?.id) {
            return [set.id];
        }
        return [];
    }
}
