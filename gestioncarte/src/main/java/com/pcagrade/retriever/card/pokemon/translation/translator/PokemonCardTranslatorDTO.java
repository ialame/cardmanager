package com.pcagrade.retriever.card.pokemon.translation.translator;

import com.pcagrade.mason.localization.Localization;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class PokemonCardTranslatorDTO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private int id;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private int weight;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean blocking = false;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Localization> localizations;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Localization> patternLocalizations;

    private String url;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Localization> getLocalizations() {
        return localizations;
    }

    public void setLocalizations(List<Localization> localizations) {
        this.localizations = localizations;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Localization> getPatternLocalizations() {
        return patternLocalizations;
    }

    public void setPatternLocalizations(List<Localization> patternLocalizations) {
        this.patternLocalizations = patternLocalizations;
    }
}
