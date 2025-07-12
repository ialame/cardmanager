package com.pcagrade.retriever.card.promo.event.extraction.handler.pokemon;

import com.pcagrade.retriever.card.promo.event.extraction.ExtractedPromoCardEventDTO;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTrait;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.Nonnull;
import java.util.List;

public class SpecificPromoEventHandler implements IPokemonPromoEventHandler {

    private static final List<String> SPECIFIC_NAMES = List.of(
            "League Challenge",
            "League Cup",
            "Pokémon League"
    );

    @Override
    public void handleEvent(@Nonnull ExtractedPromoCardEventDTO dto, @Nonnull EventNameHolder nameToParse) {
        var traits = dto.getEvent().getTraits();

        if (traits.stream().anyMatch(t -> t.is(PromoCardEventTrait.EXCLUSIVE))) {
            return;
        }
        for (var name : SPECIFIC_NAMES) {
            var trait = traits.stream()
                    .filter(t -> StringUtils.equalsIgnoreCase(t.getName(), name))
                    .findFirst()
                    .orElse(null);

            if (trait != null) {
                var translations = trait.getTranslations();

                dto.getEvent().getTranslations().forEach((l, t) -> {
                    var traitTranslation = translations.get(l);

                    if (traitTranslation != null) {
                        t.setName(traitTranslation.getName());
                        t.setLabelName(traitTranslation.getLabelName());
                    }
                });
                return;
            }
        }
    }
}
