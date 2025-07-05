package com.pcagrade.retriever.card.pokemon.source.bulbapedia;

import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction.BulbapediaExtractionStatus;
import jakarta.annotation.Nonnull;

import java.util.Comparator;

public record ExtractedBulbapediaCard(
        PokemonCardDTO card,
        BulbapediaExtractionStatus status

) {
    public static final Comparator<ExtractedBulbapediaCard> DUPLICATE_COMPARATOR = Comparator.comparing(ExtractedBulbapediaCard::status)
            .thenComparing(ExtractedBulbapediaCard::card, PokemonCardDTO.CHANGES_COMPARATOR);

    public ExtractedBulbapediaCard(@Nonnull PokemonCardDTO card) {
        this(card, BulbapediaExtractionStatus.OK);
    }

}
