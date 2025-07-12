import {PromoCardVersionDTO, Service} from "@/types";

export const promoCardVersionService = new Service<PromoCardVersionDTO, string>('/api/cards/promos/versions');
