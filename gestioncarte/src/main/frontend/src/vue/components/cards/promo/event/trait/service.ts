import {PromoCardEventTraitDTO, Service} from "@/types";

export const promoCardEventTraitService = new Service<PromoCardEventTraitDTO, string>('/api/cards/promos/events/traits');
