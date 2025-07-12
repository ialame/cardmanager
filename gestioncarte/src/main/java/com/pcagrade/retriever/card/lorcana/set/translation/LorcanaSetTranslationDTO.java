package com.pcagrade.retriever.card.lorcana.set.translation;

import com.pcagrade.mason.localization.ILocalized;
import com.pcagrade.mason.localization.Localization;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Comparator;

public class LorcanaSetTranslationDTO implements ILocalized {

    public static final Comparator<LorcanaSetTranslationDTO> CHANGES_COMPARATOR = Comparator.comparing(LorcanaSetTranslationDTO::getLocalization)
            .thenComparing(LorcanaSetTranslationDTO::getName, StringUtils::compare)
            .thenComparing(LorcanaSetTranslationDTO::getReleaseDate, ObjectUtils::compare);

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Localization localization;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean available = true;
    private LocalDate releaseDate;

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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
}
