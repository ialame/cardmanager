package com.pcagrade.retriever.card.pokemon.image;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.image.ExtractedImageDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ExtractedPokemonImagesDTO(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Ulid cardId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<ExtractedImageDTO> images

) {

    @JsonIgnore
    public int size() {
        return images.stream()
                .mapToInt(ExtractedImageDTO::size)
                .sum();
    }
}
