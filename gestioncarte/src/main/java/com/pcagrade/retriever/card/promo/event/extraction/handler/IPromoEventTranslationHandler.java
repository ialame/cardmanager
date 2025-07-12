package com.pcagrade.retriever.card.promo.event.extraction.handler;

import com.pcagrade.retriever.card.promo.event.extraction.ExtractedPromoCardEventDTO;
import com.pcagrade.retriever.card.promo.event.translation.PromoCardEventTranslationDTO;
import com.pcagrade.mason.localization.Localization;

import jakarta.annotation.Nonnull;

public interface IPromoEventTranslationHandler extends IPromoEventHandler {

    @Override
    default void handleEvent(@Nonnull ExtractedPromoCardEventDTO dto, @Nonnull EventNameHolder nameToParse) {
        dto.getEvent().getTranslations().forEach((l, t) -> handleTranslation(l, t, dto));
    }

    void handleTranslation(@Nonnull Localization localization, @Nonnull PromoCardEventTranslationDTO translation, @Nonnull ExtractedPromoCardEventDTO dto);
}
