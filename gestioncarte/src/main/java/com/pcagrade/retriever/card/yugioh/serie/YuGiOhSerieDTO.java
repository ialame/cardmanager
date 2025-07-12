package com.pcagrade.retriever.card.yugioh.serie;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.yugioh.serie.translation.YuGiOhSerieTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

public class YuGiOhSerieDTO {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Ulid id;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<Localization, YuGiOhSerieTranslationDTO> translations;

    public Ulid getId() {
        return id;
    }

    public void setId(Ulid id) {
        this.id = id;
    }


    public Map<Localization, YuGiOhSerieTranslationDTO> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<Localization, YuGiOhSerieTranslationDTO> translations) {
        this.translations = translations;
    }
}
