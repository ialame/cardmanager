package com.pcagrade.retriever.card.yugioh.translation;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pcagrade.mason.localization.ILocalized;
import com.pcagrade.mason.localization.Localization;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

@JsonSerialize(as = YuGiOhCardTranslationDTO.class)
public class YuGiOhCardTranslationDTO implements ILocalized {

    public static final Comparator<YuGiOhCardTranslationDTO> CHANGES_COMPARATOR = Comparator.comparing(YuGiOhCardTranslationDTO::getLocalization)
            .thenComparing(YuGiOhCardTranslationDTO::getNumber, StringUtils::compareIgnoreCase)
            .thenComparing(YuGiOhCardTranslationDTO::getName, StringUtils::compare)
            .thenComparing(YuGiOhCardTranslationDTO::getLabelName, StringUtils::compare)
            .thenComparing(YuGiOhCardTranslationDTO::getRarity, StringUtils::compareIgnoreCase);

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Localization localization;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String labelName;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String number;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String rarity;

    private boolean available = true;

    @Override
    public Localization getLocalization() {
        return localization;
    }

    @Override
    public void setLocalization(Localization localization) {
        this.localization = localization;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }
}
