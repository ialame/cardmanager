package com.pcagrade.retriever.card.yugioh.set.translation;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pcagrade.mason.localization.ILocalized;
import com.pcagrade.mason.localization.Localization;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Comparator;

@JsonSerialize(as = YuGiOhSetTranslationDTO.class)
public class YuGiOhSetTranslationDTO implements ILocalized {

    public static final Comparator<YuGiOhSetTranslationDTO> CHANGES_COMPARATOR = Comparator.comparing(YuGiOhSetTranslationDTO::getLocalization)
            .thenComparing(YuGiOhSetTranslationDTO::getName, StringUtils::compare)
            .thenComparing(YuGiOhSetTranslationDTO::getReleaseDate, ObjectUtils::compare);


    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Localization localization;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean available = true;
    private LocalDate releaseDate;
    private String code;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
