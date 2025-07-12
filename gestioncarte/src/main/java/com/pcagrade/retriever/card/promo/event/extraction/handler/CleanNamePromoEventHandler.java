package com.pcagrade.retriever.card.promo.event.extraction.handler;

import com.pcagrade.retriever.card.promo.event.PromoCardHelper;
import com.pcagrade.retriever.card.promo.event.extraction.ExtractedPromoCardEventDTO;

import jakarta.annotation.Nonnull;

public class CleanNamePromoEventHandler implements IPromoEventHandler {

    @Override
    public void handleEvent(@Nonnull ExtractedPromoCardEventDTO dto, @Nonnull EventNameHolder nameToParse) {
        var event = dto.getEvent();
        var name = PromoCardHelper.cleanPromoName(event.getName());

        event.setName(name);
        nameToParse.set(name);
    }
}
