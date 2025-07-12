import {Service, YuGiOhSetDTO} from "@/types";

export const yugiohSetService = new Service<YuGiOhSetDTO, string>('/api/cards/yugioh/sets');
