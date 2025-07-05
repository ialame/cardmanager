package com.pcagrade.retriever.card.promo.event.extraction.handler;

import com.pcagrade.retriever.card.promo.event.extraction.ExtractedPromoCardEventDTO;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTraitDTO;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTraitService;

import jakarta.annotation.Nonnull;
import java.util.List;

public class ExistingTraitPromoEventHandler implements ITraitPromoEventHandler {

    private final PromoCardEventTraitService promoCardEventTraitService;

    public ExistingTraitPromoEventHandler(PromoCardEventTraitService promoCardEventTraitService) {
        this.promoCardEventTraitService = promoCardEventTraitService;
    }

    @Override
    public void handleTrait(@Nonnull ExtractedPromoCardEventDTO dto, @Nonnull List<PromoCardEventTraitDTO> traits, @Nonnull EventNameHolder nameToParse) {
        var allTraits = promoCardEventTraitService.findAllByTcg(dto.getTcg());

        for (var trait : allTraits) {
            if (nameToParse.removeWord(trait.getName())) {
                traits.add(trait);
            }
        }
    }
}
