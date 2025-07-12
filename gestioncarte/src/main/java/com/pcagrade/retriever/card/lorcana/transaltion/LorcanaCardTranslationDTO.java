package com.pcagrade.retriever.card.lorcana.transaltion;

import com.pcagrade.mason.localization.ILocalized;
import com.pcagrade.mason.localization.Localization;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

public class LorcanaCardTranslationDTO implements ILocalized {

    public static final Comparator<LorcanaCardTranslationDTO> CHANGES_COMPARATOR = Comparator.comparing(LorcanaCardTranslationDTO::getLocalization)
            .thenComparing(LorcanaCardTranslationDTO::getName, StringUtils::compare)
            .thenComparing(LorcanaCardTranslationDTO::getLabelName, StringUtils::compare)
            .thenComparing(LorcanaCardTranslationDTO::getNumber, StringUtils::compare)
            .thenComparing(LorcanaCardTranslationDTO::getFullNumber, StringUtils::compare)
            .thenComparing(LorcanaCardTranslationDTO::getTitle, StringUtils::compare);

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Localization localization;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String labelName;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String number;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String fullNumber;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

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

    public String getFullNumber() {
        return fullNumber;
    }

    public void setFullNumber(String fullNumber) {
        this.fullNumber = fullNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
