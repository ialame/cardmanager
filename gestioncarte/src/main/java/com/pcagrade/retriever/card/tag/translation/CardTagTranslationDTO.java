package com.pcagrade.retriever.card.tag.translation;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.mason.localization.ILocalized;
import com.pcagrade.mason.localization.Localization;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Comparator;

@JsonSerialize(as = CardTagTranslationDTO.class)
public class CardTagTranslationDTO implements ILocalized {

    public static final Comparator<CardTagTranslationDTO> CHANGES_COMPARATOR = Comparator.comparing(CardTagTranslationDTO::getLocalization, ObjectUtils::compare)
            .thenComparing(CardTagTranslationDTO::getName, PCAUtils::compareTrimmed);

    private String name;
    private Localization localization;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Localization getLocalization() {
        return localization;
    }

    @Override
    public void setLocalization(Localization localization) {
        this.localization = localization;
    }
}
