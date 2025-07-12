package com.pcagrade.retriever.card.pokemon.source.wiki.url;

import io.swagger.v3.oas.annotations.media.Schema;

public record WikiUrlDTO(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String url,
        String cssSelector
) { }
