package com.pcagrade.retriever.card.promo.event.extraction.handler.pokemon;

import com.pcagrade.retriever.card.TradingCardGame;
import com.pcagrade.retriever.card.promo.event.PromoCardEventDTO;
import com.pcagrade.retriever.card.promo.event.extraction.ExtractedPromoCardEventDTO;
import com.pcagrade.retriever.card.promo.event.extraction.handler.ITraitPromoEventHandler;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTrait;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTraitDTO;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.Nonnull;
import java.util.List;

public class StampTraitPromoEventHandler implements ITraitPromoEventHandler, IPokemonPromoEventHandler {

    private static final List<String> STAMP_NAMES = List.of(
            PromoCardEventTrait.STAMP,
            "prerelease",
            "Build & Battle Box"
    );

    @Override
    public void handleTrait(@Nonnull ExtractedPromoCardEventDTO dto, @Nonnull List<PromoCardEventTraitDTO> traits, @Nonnull EventNameHolder nameToParse) {
        if (StringUtils.isNotBlank(nameToParse) && traits.stream().noneMatch(t -> t.is(PromoCardEventTrait.STAMP))) {
            var index = findIndex(nameToParse);

            if (index >= 0) {
                var stampName = StringUtils.trimToEmpty(StringUtils.substring(nameToParse.toString(), 0, index));

                if (StringUtils.isNotBlank(stampName)) {
                    nameToParse.removeWord(stampName);
                    traits.add(createStamp(stampName, dto.getEvent()));
                }
            }
        }
        STAMP_NAMES.forEach(nameToParse::removeWordIgnoreCase);
    }

    @Nonnull
    private static PromoCardEventTraitDTO createStamp(String name, @Nonnull PromoCardEventDTO event) {
        var stamp = ITraitPromoEventHandler.createTrait(name, PromoCardEventTrait.STAMP, TradingCardGame.POKEMON, event.getTranslations().keySet());

        for (var translation : stamp.getTranslations().values()) {
            translation.setName("Stamped");
            translation.setLabelName("Stamped");
        }
        return stamp;
    }

    private static int findIndex(@Nonnull EventNameHolder nameToParse) {
        var index = -1;

        for (var name : STAMP_NAMES) {
            var traitIndex = StringUtils.indexOfIgnoreCase(nameToParse, name);

            if (traitIndex >= 0) {
                index = index >= 0 ? Math.min(index, traitIndex) : traitIndex;
            }
        }
        return index;
    }
}
