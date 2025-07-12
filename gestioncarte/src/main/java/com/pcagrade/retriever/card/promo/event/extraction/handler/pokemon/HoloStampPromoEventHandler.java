package com.pcagrade.retriever.card.promo.event.extraction.handler.pokemon;

import com.pcagrade.retriever.card.promo.event.extraction.ExtractedPromoCardEventDTO;
import com.pcagrade.retriever.card.promo.event.extraction.handler.IPromoEventTranslationNameHandler;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTrait;
import com.pcagrade.mason.localization.Localization;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicReference;

public class HoloStampPromoEventHandler implements IPromoEventTranslationNameHandler, IPokemonPromoEventHandler {

    @Override
    public String handleName(@Nonnull String name, @Nonnull Localization localization, @Nonnull ExtractedPromoCardEventDTO dto) {
        var traits = dto.getEvent().getTraits();

        if (traits.stream().anyMatch(t -> t.is(PromoCardEventTrait.STAMP))) {
            var atomicName = new AtomicReference<>(name);

            traits.stream()
                    .filter(t -> t.is(PromoCardEventTrait.HOLO))
                    .forEach(t -> atomicName.set(StringUtils.removeIgnoreCase(atomicName.get(), t.getName())));
            return atomicName.get();
        }
        return name;
    }
}
