package com.pcagrade.retriever.card.lorcana.serie;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.lorcana.serie.translation.LorcanaSerieTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.EnumMap;
import java.util.Map;

public class LorcanaSerieDTO {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Ulid id;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<Localization, LorcanaSerieTranslationDTO> translations = new EnumMap<>(Localization.class);

    public Ulid getId() {
        return id;
    }

    public void setId(Ulid id) {
        this.id = id;
    }


    public Map<Localization, LorcanaSerieTranslationDTO> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<Localization, LorcanaSerieTranslationDTO> translations) {
        this.translations = translations;
    }
}
