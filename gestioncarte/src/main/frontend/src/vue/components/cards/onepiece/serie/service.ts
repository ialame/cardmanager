import {OnePieceSerieDTO, Service} from "@/types";

export const onePieceSerieService = new Service<OnePieceSerieDTO, string>('/api/cards/onepiece/series');
