package com.pcagrade.retriever.card.lorcana;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.localization.LocalizationUtils;
import com.pcagrade.mason.ulid.UlidHelper;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.lorcana.transaltion.LorcanaCardTranslationDTO;
import com.pcagrade.retriever.card.tag.CardTagDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class LorcanaCardDTO {

    public static final Comparator<LorcanaCardDTO> CHANGES_COMPARATOR = Comparator.comparing(LorcanaCardDTO::getArtist, StringUtils::compare)
            .thenComparing(LorcanaCardDTO::getArtist, StringUtils::compare)
            .thenComparing(LorcanaCardDTO::getRarity, StringUtils::compare)
            .thenComparing(LorcanaCardDTO::getInk, StringUtils::compare)
            .thenComparing(LorcanaCardDTO::isReprint)
            .thenComparing((c1, c2) -> LocalizationUtils.translationComparator(LorcanaCardTranslationDTO.CHANGES_COMPARATOR).compare(c1.getTranslations(), c2.getTranslations()))
            .thenComparing(LorcanaCardDTO::getTags, PCAUtils.collectionComparator(CardTagDTO.CHANGES_COMPARATOR))
            .thenComparing(LorcanaCardDTO::getSetIds, PCAUtils.collectionComparator(UlidHelper::compare));

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Ulid id;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<Localization, LorcanaCardTranslationDTO> translations;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Ulid> setIds;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<CardTagDTO> tags;
    private String idPrim;
    private String artist;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String rarity;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String ink;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean reprint;


    public Ulid getId() {
        return id;
    }

    public void setId(Ulid id) {
        this.id = id;
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

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getInk() {
        return ink;
    }

    public void setInk(String ink) {
        this.ink = ink;
    }

    public boolean isReprint() {
        return reprint;
    }

    public void setReprint(boolean reprint) {
        this.reprint = reprint;
    }

    public Map<Localization, LorcanaCardTranslationDTO> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<Localization, LorcanaCardTranslationDTO> translations) {
        this.translations = translations;
    }
}
