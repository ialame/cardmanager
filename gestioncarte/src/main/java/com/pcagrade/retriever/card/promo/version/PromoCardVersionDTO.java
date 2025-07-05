package com.pcagrade.retriever.card.promo.version;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.TradingCardGame;
import com.pcagrade.retriever.card.promo.version.translation.PromoCardVersionTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.EnumMap;
import java.util.Map;

public class PromoCardVersionDTO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Ulid id;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean hidden = false;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private TradingCardGame tcg;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<Localization, PromoCardVersionTranslationDTO> translations = new EnumMap<>(Localization.class);
    
    public Ulid getId() {
        return id;
    }

    public void setId(Ulid id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Map<Localization, PromoCardVersionTranslationDTO> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<Localization, PromoCardVersionTranslationDTO> translations) {
        this.translations = translations;
    }

    public TradingCardGame getTcg() {
        return tcg;
    }

    public void setTcg(TradingCardGame tcg) {
        this.tcg = tcg;
    }
}
