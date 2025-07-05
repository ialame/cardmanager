package com.pcagrade.retriever.card.promo.event.extraction.handler.pokemon;

import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetService;
import com.pcagrade.retriever.card.promo.event.extraction.ExtractedPromoCardEventDTO;
import com.pcagrade.retriever.card.promo.event.extraction.handler.IAddTranslationEventHandler;
import com.pcagrade.mason.localization.Localization;

import jakarta.annotation.Nonnull;

public class AddPokemonTranslationEventHandler implements IAddTranslationEventHandler, IPokemonPromoEventHandler {

    private final PokemonSetService pokemonSetService;

    public AddPokemonTranslationEventHandler(PokemonSetService pokemonSetService) {
        this.pokemonSetService = pokemonSetService;
    }

    @Override
    public void handleEvent(@Nonnull ExtractedPromoCardEventDTO dto, @Nonnull EventNameHolder nameToParse) {
        initTranslations(dto.getEvent(), dto.getSetIds().stream()
                .<PokemonSetDTO>mapMulti((i, downstream) -> pokemonSetService.findSet(i).ifPresent(downstream))
                .<Localization>mapMulti((s, downstream) -> s.getTranslations().forEach((l, t) -> {
                    if (t.isAvailable()) {
                        downstream.accept(l);
                    }
                }))
                .distinct()
                .toList());
    }
}
