package com.pcagrade.retriever.card.promo.event.extraction.handler;

import com.pcagrade.retriever.card.promo.event.extraction.ExtractedPromoCardEventDTO;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTrait;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTraitDTO;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.Nonnull;
import java.util.List;

public class FinalTraitPromoEventHandler implements ITraitPromoEventHandler {

    @Override
    public void handleTrait(@Nonnull ExtractedPromoCardEventDTO dto, @Nonnull List<PromoCardEventTraitDTO> traits, @Nonnull EventNameHolder nameToParse) {
        if (StringUtils.isNotBlank(nameToParse) && !StringUtils.equalsIgnoreCase(nameToParse, "promo")) {
            traits.add(ITraitPromoEventHandler.createTrait(nameToParse, PromoCardEventTrait.EVENT, dto.getTcg(), dto.getEvent().getTranslations().keySet()));
        }
    }
}
