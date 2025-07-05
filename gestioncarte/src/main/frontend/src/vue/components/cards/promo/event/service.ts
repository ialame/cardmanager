import {PromoCardEventDTO, Service} from "@/types";

export const promoCardEventService = new Service<PromoCardEventDTO, string>('/api/cards/promos/events');
