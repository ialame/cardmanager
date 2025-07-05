package com.pcagrade.retriever.card.promo.event.extraction.handler;

import com.pcagrade.retriever.card.promo.event.extraction.ExtractedPromoCardEventDTO;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTraitDTO;

import jakarta.annotation.Nonnull;
import java.util.List;

public class SortTraitPromoEventHandler implements ITraitPromoEventHandler {
    @Override
    public void handleTrait(@Nonnull ExtractedPromoCardEventDTO dto, @Nonnull List<PromoCardEventTraitDTO> traits, @Nonnull EventNameHolder nameToParse) {
        traits.sort(PromoCardEventTraitDTO.COMPARATOR);
    }
}
