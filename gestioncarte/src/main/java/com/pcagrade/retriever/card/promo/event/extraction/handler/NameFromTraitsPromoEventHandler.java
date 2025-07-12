package com.pcagrade.retriever.card.promo.event.extraction.handler;

import com.pcagrade.retriever.card.TradingCardGame;
import com.pcagrade.retriever.card.promo.event.extraction.ExtractedPromoCardEventDTO;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTrait;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTraitDTO;
import com.pcagrade.retriever.card.promo.event.translation.PromoCardEventTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class NameFromTraitsPromoEventHandler implements IPromoEventTranslationHandler {

    @Override
    public void handleEvent(@Nonnull ExtractedPromoCardEventDTO dto, @Nonnull EventNameHolder nameToParse) {
        if (dto.getTcg() != TradingCardGame.POKEMON || hasTranslations(dto.getEvent().getTraits())) {
            IPromoEventTranslationHandler.super.handleEvent(dto, nameToParse);
        } else {
            clearTranslation(dto.getEvent().getTranslations());
        }
    }

    @Override
    public void handleTranslation(@Nonnull Localization localization, @Nonnull PromoCardEventTranslationDTO translation, @Nonnull ExtractedPromoCardEventDTO dto) {
        dto.getEvent().getTraits().forEach(t -> {
            var traitTranslation = t.getTranslations().get(localization);

            if (traitTranslation == null) {
                return;
            }
            if (StringUtils.isNotBlank(traitTranslation.getName())) {
                translation.setName(StringUtils.replaceIgnoreCase(translation.getName(), t.getName(), traitTranslation.getName()));
            }
            if (StringUtils.isNotBlank(traitTranslation.getLabelName())) {
                translation.setLabelName(StringUtils.replaceIgnoreCase(translation.getLabelName(), t.getName(), traitTranslation.getLabelName()));
            }
        });
    }

    private void clearTranslation(Map<Localization, PromoCardEventTranslationDTO> translations) {
        translations.forEach((l, t) -> {
            t.setName("");
            t.setLabelName("");
        });
    }

    private boolean hasTranslations(List<PromoCardEventTraitDTO> traits) {
        var emptyEvent = false;
        var exclusive = false;
        var holo = false;
        var stamp = false;

        for (var trait : traits) {
            if (trait.getId() == null && trait.is(PromoCardEventTrait.EVENT)) {
                emptyEvent = true;
            } else if (trait.is(PromoCardEventTrait.EXCLUSIVE)) {
                exclusive = true;
            } else if (trait.is(PromoCardEventTrait.HOLO)) {
                holo = true;
            } else if (trait.is(PromoCardEventTrait.STAMP)) {
                stamp = true;
            }
        }
        return !emptyEvent || (exclusive && (holo || stamp)) || (holo && stamp);
    }
}
