package com.pcagrade.retriever.card.promo.event.extraction.handler.pokemon;

import com.pcagrade.retriever.card.promo.event.extraction.ExtractedPromoCardEventDTO;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.Nonnull;
import java.util.List;

public class ChampionshipPromoEventHandler implements IPokemonPromoEventHandler {

    private static final List<String> CHAMPIONSHIP_NAMES = List.of(
            "Prerelease",
            "World Championship",
            "Battle Academy"
    );

    @Override
    public void handleEvent(@Nonnull ExtractedPromoCardEventDTO dto, @Nonnull EventNameHolder nameToParse) {
        var event = dto.getEvent();

        if (CHAMPIONSHIP_NAMES.stream().anyMatch(s -> StringUtils.containsIgnoreCase(event.getName(), s))) {
            event.setChampionship(true);
        }
    }
}
