package com.pcagrade.retriever.card.promo.event.extraction.handler;

import com.pcagrade.retriever.card.promo.event.extraction.ExtractedPromoCardEventDTO;
import com.pcagrade.retriever.card.promo.event.translation.PromoCardEventTranslationDTO;
import com.pcagrade.mason.localization.Localization;

import jakarta.annotation.Nonnull;

public interface IPromoEventTranslationNameHandler extends IPromoEventTranslationHandler {

    @Override
    default void handleTranslation(@Nonnull Localization localization, @Nonnull PromoCardEventTranslationDTO translation, @Nonnull ExtractedPromoCardEventDTO dto) {
        translation.setName(handleName(translation.getName(), localization, dto));
        translation.setLabelName(handleLabelName(translation.getLabelName(), localization, dto));
    }

    String handleName(@Nonnull String name, @Nonnull Localization localization, @Nonnull ExtractedPromoCardEventDTO dto);

    default String handleLabelName(@Nonnull String labelName, @Nonnull Localization localization, @Nonnull ExtractedPromoCardEventDTO dto) {
        return handleName(labelName, localization, dto);
    }

}
