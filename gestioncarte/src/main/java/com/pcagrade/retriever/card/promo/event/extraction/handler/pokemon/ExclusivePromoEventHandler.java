package com.pcagrade.retriever.card.promo.event.extraction.handler.pokemon;

import com.pcagrade.retriever.card.promo.event.extraction.ExtractedPromoCardEventDTO;
import com.pcagrade.retriever.card.promo.event.extraction.handler.IPromoEventTranslationHandler;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTrait;
import com.pcagrade.retriever.card.promo.event.translation.PromoCardEventTranslationDTO;
import com.pcagrade.mason.localization.Localization;

import jakarta.annotation.Nonnull;

public class ExclusivePromoEventHandler implements IPromoEventTranslationHandler, IPokemonPromoEventHandler {

    private final String type;

    public ExclusivePromoEventHandler(String type) {
        this.type = type;
    }

    @Override
    public void handleTranslation(@Nonnull Localization localization, @Nonnull PromoCardEventTranslationDTO translation, @Nonnull ExtractedPromoCardEventDTO dto) {
        var traits = dto.getEvent().getTraits();

        if (traits.stream().anyMatch(t -> t.is(PromoCardEventTrait.EXCLUSIVE))) {
            traits.stream()
                    .filter(t -> t.is(type))
                    .findFirst()
                    .ifPresent(t -> {
                        var traitTranslation = t.getTranslations().get(localization);

                        if (traitTranslation != null) {
                            translation.setName(traitTranslation.getName());
                            translation.setLabelName(traitTranslation.getLabelName());
                        }
                    });
        }
    }
}
