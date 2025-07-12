import {OnePieceSetDTO, Service} from "@/types";

export const onePieceSetService = new Service<OnePieceSetDTO, string>('/api/cards/onepiece/sets');
