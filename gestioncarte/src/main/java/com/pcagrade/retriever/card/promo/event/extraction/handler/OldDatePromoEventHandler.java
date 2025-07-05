package com.pcagrade.retriever.card.promo.event.extraction.handler;

import com.pcagrade.retriever.card.promo.event.PromoCardEventService;
import com.pcagrade.retriever.card.promo.event.extraction.ExtractedPromoCardEventDTO;

import jakarta.annotation.Nonnull;

@Deprecated(forRemoval = true)
public class OldDatePromoEventHandler implements IPromoEventHandler {

    private final PromoCardEventService promoCardEventService;

    public OldDatePromoEventHandler(PromoCardEventService promoCardEventService) {
        this.promoCardEventService = promoCardEventService;
    }

    @Override
    public void handleEvent(@Nonnull ExtractedPromoCardEventDTO dto, @Nonnull EventNameHolder nameToParse) {
        var event = dto.getEvent();
        var id = event.getId();

        if (id == null) {
            return;
        }

        var oldDate = promoCardEventService.findOldDate(id);

        if (oldDate  == null) {
            return;
        }

        event.getTranslations().forEach((l, t) -> {
            if (t != null && t.getReleaseDate() == null) {
                t.setReleaseDate(oldDate);
            }
        });
    }
}
