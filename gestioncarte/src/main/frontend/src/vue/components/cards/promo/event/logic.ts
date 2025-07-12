import {promoCardEventService} from "@components/cards/promo";
import rest from "@/rest";
import {ExtractedPromoCardEventDTO} from "@/types";

export const savePromoEvent = async (editEvent: ExtractedPromoCardEventDTO) => {
    if (!editEvent.event) {
        return;
    }
    const event = await promoCardEventService.save(editEvent.event);

    await Promise.all((editEvent.promos ?? []).map(async promo => {
        promo.eventId = event.id;
        if (promo.id) {
            await rest.put(`/api/cards/promos`, {data: promo});
        } else {
            await rest.post(`/api/cards/promos`, {data: promo});
        }
    }));

    await rest.delete(`/api/cards/promos/events/extracted/${editEvent.id}`);
    return event;
}
