package com.pcagrade.retriever.card.onepiece.serie;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.onepiece.serie.translation.OnePieceSerieTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.EnumMap;
import java.util.Map;

public class OnePieceSerieDTO {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Ulid id;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<Localization, OnePieceSerieTranslationDTO> translations = new EnumMap<>(Localization.class);

    public Ulid getId() {
        return id;
    }

    public void setId(Ulid id) {
        this.id = id;
    }


    public Map<Localization, OnePieceSerieTranslationDTO> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<Localization, OnePieceSerieTranslationDTO> translations) {
        this.translations = translations;
    }
}
