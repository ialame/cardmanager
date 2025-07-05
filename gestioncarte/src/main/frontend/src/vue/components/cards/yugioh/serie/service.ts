import {Service, YuGiOhSerieDTO} from "@/types";

export const yugiohSerieService = new Service<YuGiOhSerieDTO, string>('/api/cards/yugioh/series');
