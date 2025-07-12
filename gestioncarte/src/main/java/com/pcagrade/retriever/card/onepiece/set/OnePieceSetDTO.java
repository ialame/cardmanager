package com.pcagrade.retriever.card.onepiece.set;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.onepiece.set.translation.OnePieceSetTranslationDTO;
import com.pcagrade.retriever.card.onepiece.source.official.id.OnePieceOfficialSiteIdDTO;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.localization.LocalizationUtils;
import com.pcagrade.mason.ulid.UlidHelper;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class OnePieceSetDTO {

    public static final Comparator<OnePieceSetDTO> CHANGES_COMPARATOR = Comparator.comparing(OnePieceSetDTO::getSerieId, UlidHelper::compare)
            .thenComparing(OnePieceSetDTO::isPromo)
            .thenComparing(OnePieceSetDTO::getCode, StringUtils::compare)
            .thenComparing((c1, c2) -> LocalizationUtils.translationComparator(OnePieceSetTranslationDTO.CHANGES_COMPARATOR).compare(c1.getTranslations(), c2.getTranslations()))
            .thenComparing(OnePieceSetDTO::getOfficialSiteIds, PCAUtils.collectionComparator(OnePieceOfficialSiteIdDTO.CHANGES_COMPARATOR));

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Ulid id;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<Localization, OnePieceSetTranslationDTO> translations = new EnumMap<>(Localization.class);
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Ulid serieId;
    private String code;
    private boolean promo;
    private List<OnePieceOfficialSiteIdDTO> officialSiteIds;

    public Ulid getId() {
        return id;
    }

    public void setId(Ulid id) {
        this.id = id;
    }

    public Map<Localization, OnePieceSetTranslationDTO> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<Localization, OnePieceSetTranslationDTO> translations) {
        this.translations = translations;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isPromo() {
        return promo;
    }

    public void setPromo(boolean promo) {
        this.promo = promo;
    }

    public Ulid getSerieId() {
        return serieId;
    }

    public void setSerieId(Ulid serieId) {
        this.serieId = serieId;
    }

    public List<OnePieceOfficialSiteIdDTO> getOfficialSiteIds() {
        return officialSiteIds;
    }

    public void setOfficialSiteIds(List<OnePieceOfficialSiteIdDTO> officialSiteIds) {
        this.officialSiteIds = officialSiteIds;
    }
}
