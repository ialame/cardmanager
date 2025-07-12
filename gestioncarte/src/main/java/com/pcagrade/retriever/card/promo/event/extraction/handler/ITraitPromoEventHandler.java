package com.pcagrade.retriever.card.promo.event.extraction.handler;

import com.pcagrade.retriever.card.TradingCardGame;
import com.pcagrade.retriever.card.promo.event.extraction.ExtractedPromoCardEventDTO;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTraitDTO;
import com.pcagrade.retriever.card.promo.event.trait.translation.PromoCardEventTraitTranslationDTO;
import com.pcagrade.mason.localization.Localization;

import jakarta.annotation.Nonnull;
import java.util.List;

public interface ITraitPromoEventHandler extends IPromoEventHandler {

    @Nonnull
    static PromoCardEventTraitDTO createTrait(@Nonnull CharSequence name, @Nonnull String type, @Nonnull TradingCardGame tcg, Iterable<Localization> localizations) {
        var trait = new PromoCardEventTraitDTO();

        trait.setName(name.toString());
        trait.setType(type);
        trait.setTcg(tcg);

        var translations = trait.getTranslations();

        for (Localization localization : localizations) {
            var translation = new PromoCardEventTraitTranslationDTO();

            translation.setLocalization(localization);
            translation.setName(name.toString());
            translation.setLabelName(name.toString());
            translations.put(localization, translation);
        }
        return trait;
    }

    @Override
    default void handleEvent(@Nonnull ExtractedPromoCardEventDTO dto, @Nonnull EventNameHolder nameToParse) {
        var event = dto.getEvent();

        handleTrait(dto, event.getTraits(), nameToParse);
    }

    void handleTrait(@Nonnull ExtractedPromoCardEventDTO dto, @Nonnull List<PromoCardEventTraitDTO> traits, @Nonnull EventNameHolder nameToParse);
}
