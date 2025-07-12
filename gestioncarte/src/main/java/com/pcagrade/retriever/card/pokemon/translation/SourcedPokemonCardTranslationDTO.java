package com.pcagrade.retriever.card.pokemon.translation;

import com.pcagrade.retriever.extraction.consolidation.source.ConsolidationSource;
import com.pcagrade.retriever.extraction.consolidation.source.IConsolidationSource;
import com.pcagrade.retriever.extraction.consolidation.source.ILinkedConsolidationSource;
import com.pcagrade.retriever.extraction.consolidation.source.INamedConsolidationSource;
import com.pcagrade.mason.localization.Localization;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.annotation.Nonnull;

public record SourcedPokemonCardTranslationDTO(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Localization localization,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String source,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        int weight,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String link,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        PokemonCardTranslationDTO translation
) implements INamedConsolidationSource<PokemonCardTranslationDTO>, ILinkedConsolidationSource<PokemonCardTranslationDTO> {

    @Nonnull
    @Override
    public <U> IConsolidationSource<U> with(U newValue) {
        return new ConsolidationSource<>(newValue, weight); // TODO keep source, localization and link
    }

    @Nonnull
    @Override
    public PokemonCardTranslationDTO getValue() {
        return translation;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public String getLink() {
        return link;
    }

    @Override
    public String getName() {
        return source;
    }
}
