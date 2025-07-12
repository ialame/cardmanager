package com.pcagrade.retriever.card.promo.event.extraction.handler;

import com.pcagrade.retriever.card.promo.event.extraction.ExtractedPromoCardEventDTO;
import com.pcagrade.mason.localization.Localization;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.Nonnull;

public class FinalCleanPromoEventHandler implements IPromoEventTranslationNameHandler {

    @Override
    public String handleName(@Nonnull String name, @Nonnull Localization localization, @Nonnull ExtractedPromoCardEventDTO dto) {
        return StringUtils.trimToEmpty(StringUtils.removeEndIgnoreCase(name, "promo"));
    }
}
