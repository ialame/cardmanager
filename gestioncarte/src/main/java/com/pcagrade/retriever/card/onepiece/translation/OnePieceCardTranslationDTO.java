package com.pcagrade.retriever.card.onepiece.translation;

import com.pcagrade.mason.localization.ILocalized;
import com.pcagrade.mason.localization.Localization;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

public class OnePieceCardTranslationDTO implements ILocalized {

    public static final Comparator<OnePieceCardTranslationDTO> CHANGES_COMPARATOR = Comparator.comparing(OnePieceCardTranslationDTO::getLocalization)
            .thenComparing(OnePieceCardTranslationDTO::getName, StringUtils::compare)
            .thenComparing(OnePieceCardTranslationDTO::getLabelName, StringUtils::compare);

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Localization localization;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String labelName;

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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
