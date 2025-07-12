package com.pcagrade.retriever.card.yugioh;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.extraction.status.CardExtractionStatusDTO;
import com.pcagrade.retriever.card.foil.Foil;
import com.pcagrade.retriever.card.tag.CardTagDTO;
import com.pcagrade.retriever.card.yugioh.translation.YuGiOhCardTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.localization.LocalizationUtils;
import com.pcagrade.mason.ulid.UlidHelper;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@JsonSerialize(as = YuGiOhCardDTO.class)
public class YuGiOhCardDTO {

    public static final Comparator<YuGiOhCardDTO> CHANGES_COMPARATOR = Comparator.comparing(YuGiOhCardDTO::getArtist, StringUtils::compare)
            .thenComparing(YuGiOhCardDTO::isSneakPeek)
            .thenComparingInt(YuGiOhCardDTO::getLevel)
            .thenComparing((c1, c2) -> LocalizationUtils.translationComparator(YuGiOhCardTranslationDTO.CHANGES_COMPARATOR).compare(c1.getTranslations(), c2.getTranslations()))
            .thenComparing(YuGiOhCardDTO::getTags, PCAUtils.collectionComparator(CardTagDTO.CHANGES_COMPARATOR))
            .thenComparing(YuGiOhCardDTO::getSetIds, PCAUtils.collectionComparator(UlidHelper::compare));

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Ulid id;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> types = new ArrayList<>();
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<Localization, YuGiOhCardTranslationDTO> translations = new EnumMap<>(Localization.class);
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Ulid> setIds = new ArrayList<>();
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<CardTagDTO> tags;
    private String idPrim;
    private CardExtractionStatusDTO extractionStatus;
    private String artist;
    private int level;
    private Foil foil;
    private boolean sneakPeek;

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<Ulid> getSetIds() {
        return setIds;
    }

    public void setSetIds(List<Ulid> setIds) {
        this.setIds = setIds;
    }

    public List<CardTagDTO> getTags() {
        return tags;
    }

    public void setTags(List<CardTagDTO> tags) {
        this.tags = tags;
    }

    public String getIdPrim() {
        return idPrim;
    }

    public void setIdPrim(String idPrim) {
        this.idPrim = idPrim;
    }

    public CardExtractionStatusDTO getExtractionStatus() {
        return extractionStatus;
    }

    public void setExtractionStatus(CardExtractionStatusDTO extractionStatus) {
        this.extractionStatus = extractionStatus;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Map<Localization, YuGiOhCardTranslationDTO> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<Localization, YuGiOhCardTranslationDTO> translations) {
        this.translations = translations;
    }

    public Foil getFoil() {
        return foil;
    }

    public void setFoil(Foil foil) {
        this.foil = foil;
    }

    public Ulid getId() {
        return id;
    }

    public void setId(Ulid id) {
        this.id = id;
    }

    public boolean isSneakPeek() {
        return sneakPeek;
    }

    public void setSneakPeek(boolean sneakPeek) {
        this.sneakPeek = sneakPeek;
    }
}
