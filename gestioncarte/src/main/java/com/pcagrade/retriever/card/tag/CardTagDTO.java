package com.pcagrade.retriever.card.tag;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.ulid.UlidHelper;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.tag.translation.CardTagTranslationDTO;
import com.pcagrade.retriever.card.tag.type.CardTagType;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.localization.LocalizationUtils;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;

@JsonSerialize(as = CardTagDTO.class)
public class CardTagDTO {

    public static final Comparator<CardTagDTO> CHANGES_COMPARATOR = Comparator.comparing(CardTagDTO::getId, UlidHelper::compare)
            .thenComparing(CardTagDTO::getType, Comparator.comparing(CardTagType::getCode, PCAUtils::compareTrimmed))
            .thenComparing((t1, t2) -> LocalizationUtils.translationComparator(CardTagTranslationDTO.CHANGES_COMPARATOR).compare(t1.getTranslations(), t2.getTranslations()));

    private Ulid id;
    private CardTagType type;

    private Map<Localization, CardTagTranslationDTO> translations = new EnumMap<>(Localization.class);

    public CardTagType getType() {
        return type;
    }

    public void setType(CardTagType type) {
        this.type = type;
    }

    public Map<Localization, CardTagTranslationDTO> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<Localization, CardTagTranslationDTO> translations) {
        this.translations = translations;
    }

    public Ulid getId() {
        return id;
    }

    public void setId(Ulid id) {
        this.id = id;
    }
}
