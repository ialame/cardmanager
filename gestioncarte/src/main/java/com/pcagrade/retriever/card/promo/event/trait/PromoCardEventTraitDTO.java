package com.pcagrade.retriever.card.promo.event.trait;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.TradingCardGame;
import com.pcagrade.retriever.card.promo.event.trait.translation.PromoCardEventTraitTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.ulid.UlidHelper;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;

public class PromoCardEventTraitDTO {

    public static final Comparator<PromoCardEventTraitDTO> COMPARATOR = Comparator.comparing(PromoCardEventTraitDTO::getType, StringUtils::compare)
            .thenComparing(PromoCardEventTraitDTO::getName, StringUtils::compare)
            .thenComparing(PromoCardEventTraitDTO::getId, UlidHelper::compare)
            .thenComparing(PromoCardEventTraitDTO::getTcg);
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Ulid id;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private TradingCardGame tcg;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<Localization, PromoCardEventTraitTranslationDTO> translations = new EnumMap<>(Localization.class);

    public PromoCardEventTraitDTO() {
    }

    public Ulid getId() {
        return id;
    }

    public void setId(Ulid id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public boolean is(String type) {
        if (StringUtils.isBlank(type)) {
            return false;
        }
        return StringUtils.equalsIgnoreCase(this.type, type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Localization, PromoCardEventTraitTranslationDTO> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<Localization, PromoCardEventTraitTranslationDTO> translations) {
        this.translations = translations;
    }

    public TradingCardGame getTcg() {
        return tcg;
    }

    public void setTcg(TradingCardGame tcg) {
        this.tcg = tcg;
    }
}
