package com.pcagrade.retriever.card.promo.event;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTraitDTO;
import com.pcagrade.retriever.card.promo.event.translation.PromoCardEventTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PromoCardEventDTO {

    public static final Comparator<PromoCardEventDTO> CHANGES_COMPARATOR = Comparator.comparing(PromoCardEventDTO::getName, StringUtils::compare)
            .thenComparing(PromoCardEventDTO::isHidden)
            .thenComparing(PromoCardEventDTO::isChampionship)
            .thenComparing(PromoCardEventDTO::isWithoutDate)
            .thenComparing(PromoCardEventDTO::isAlwaysDisplayed);
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Ulid id;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean hidden = false;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean championship = false;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean withoutDate = false;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean alwaysDisplayed = false;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<Localization, PromoCardEventTranslationDTO> translations = new EnumMap<>(Localization.class);

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<PromoCardEventTraitDTO> traits = new ArrayList<>();

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

    public boolean isChampionship() {
        return championship;
    }

    public void setChampionship(boolean championship) {
        this.championship = championship;
    }

    public boolean isWithoutDate() {
        return withoutDate;
    }

    public void setWithoutDate(boolean withoutDate) {
        this.withoutDate = withoutDate;
    }

    public boolean isAlwaysDisplayed() {
        return alwaysDisplayed;
    }

    public void setAlwaysDisplayed(boolean alwaysDisplayed) {
        this.alwaysDisplayed = alwaysDisplayed;
    }

    public Map<Localization, PromoCardEventTranslationDTO> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<Localization, PromoCardEventTranslationDTO> translations) {
        this.translations = translations;
    }

    public List<PromoCardEventTraitDTO> getTraits() {
        return traits;
    }

    public void setTraits(List<PromoCardEventTraitDTO> traits) {
        this.traits = traits;
    }
}
