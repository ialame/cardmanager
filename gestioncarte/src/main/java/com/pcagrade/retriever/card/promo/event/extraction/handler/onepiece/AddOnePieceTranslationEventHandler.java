package com.pcagrade.retriever.card.promo.event.extraction.handler.onepiece;

import com.pcagrade.retriever.card.promo.event.extraction.ExtractedPromoCardEventDTO;
import com.pcagrade.retriever.card.promo.event.extraction.handler.IAddTranslationEventHandler;
import com.pcagrade.retriever.card.promo.event.extraction.handler.IPromoEventHandler;
import com.pcagrade.mason.localization.Localization;

import jakarta.annotation.Nonnull;

public class AddOnePieceTranslationEventHandler implements IAddTranslationEventHandler, IOnePiecePromoEventHandler {
    @Override
    public void handleEvent(@Nonnull ExtractedPromoCardEventDTO dto, @Nonnull IPromoEventHandler.EventNameHolder nameToParse) {
        initTranslations(dto.getEvent(), dto.getPromos().stream()
                .<Localization>mapMulti((p, downstream) -> downstream.accept(p.getLocalization()))
                .distinct()
                .toList());
    }

}
