package com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction.handler;

import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.BulbapediaPokemonCard;
import com.pcagrade.mason.localization.Localization;

@FunctionalInterface
public interface IBulbapediaMappingHandler {
    void handle(PokemonCardDTO dto, BulbapediaPokemonCard card, Localization localization);
}
