package com.pcagrade.retriever.card.onepiece;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.extraction.status.CardExtractionStatusDTO;
import com.pcagrade.retriever.card.onepiece.translation.OnePieceCardTranslationDTO;
import com.pcagrade.retriever.card.promo.PromoCardDTO;
import com.pcagrade.retriever.card.tag.CardTagDTO;
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

public class OnePieceCardDTO {

    public static final Comparator<OnePieceCardDTO> CHANGES_COMPARATOR = Comparator.comparing(OnePieceCardDTO::getArtist, StringUtils::compare)
            .thenComparing(OnePieceCardDTO::getNumber, StringUtils::compare)
            .thenComparing(OnePieceCardDTO::getType, StringUtils::compare)
            .thenComparing(OnePieceCardDTO::getAttribute, StringUtils::compare)
            .thenComparing(OnePieceCardDTO::getColor, StringUtils::compare)
            .thenComparing(OnePieceCardDTO::getRarity, StringUtils::compare)
            .thenComparing(OnePieceCardDTO::getParallel)
            .thenComparing((c1, c2) -> LocalizationUtils.translationComparator(OnePieceCardTranslationDTO.CHANGES_COMPARATOR).compare(c1.getTranslations(), c2.getTranslations()))
            .thenComparing(OnePieceCardDTO::getTags, PCAUtils.collectionComparator(CardTagDTO.CHANGES_COMPARATOR))
            .thenComparing(OnePieceCardDTO::getSetIds, PCAUtils.collectionComparator(UlidHelper::compare))
            .thenComparing(OnePieceCardDTO::getPromos, PCAUtils.collectionComparator(PromoCardDTO.CHANGES_COMPARATOR));

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Ulid id;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String number;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String attribute;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String color;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String rarity;
    private int parallel;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<Localization, OnePieceCardTranslationDTO> translations = new EnumMap<>(Localization.class);
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Ulid> setIds = new ArrayList<>();
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<CardTagDTO> tags;
    private String idPrim;
    private CardExtractionStatusDTO extractionStatus;
    private String artist;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<PromoCardDTO> promos = new ArrayList<>();

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

    public Map<Localization, OnePieceCardTranslationDTO> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<Localization, OnePieceCardTranslationDTO> translations) {
        this.translations = translations;
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

    public void setType(String type) {
        this.type = type;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public int getParallel() {
        return parallel;
    }

    public void setParallel(int parallel) {
        this.parallel = parallel;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public List<PromoCardDTO> getPromos() {
        return promos;
    }

    public void setPromos(List<PromoCardDTO> promos) {
        this.promos = promos;
    }
}
