package com.pcagrade.retriever.card.yugioh.set;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.yugioh.set.translation.YuGiOhSetTranslationDTO;
import com.pcagrade.retriever.card.yugioh.source.official.pid.OfficialSitePidDTO;
import com.pcagrade.retriever.card.yugioh.source.yugipedia.set.YugipediaSetDTO;
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

@JsonSerialize(as = YuGiOhSetDTO.class)
public class YuGiOhSetDTO {

    public static final Comparator<YuGiOhSetDTO> CHANGES_COMPARATOR = Comparator.comparing(YuGiOhSetDTO::getSerieId, UlidHelper::compare)
            .thenComparing(YuGiOhSetDTO::isPromo)
            .thenComparing(YuGiOhSetDTO::getType, StringUtils::compare)
            .thenComparing((c1, c2) -> LocalizationUtils.translationComparator(YuGiOhSetTranslationDTO.CHANGES_COMPARATOR).compare(c1.getTranslations(), c2.getTranslations()))
            .thenComparing(YuGiOhSetDTO::getOfficialSitePids, PCAUtils.collectionComparator(OfficialSitePidDTO.CHANGES_COMPARATOR))
            .thenComparing(YuGiOhSetDTO::getYugipediaSets, PCAUtils.collectionComparator(YugipediaSetDTO.CHANGES_COMPARATOR));

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Ulid id;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<Localization, YuGiOhSetTranslationDTO> translations = new EnumMap<>(Localization.class);
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Ulid serieId;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean promo;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;
    private Ulid parentId;

    private List<OfficialSitePidDTO> officialSitePids = new ArrayList<>();
    private List<YugipediaSetDTO> yugipediaSets = new ArrayList<>();

    public Ulid getId() {
        return id;
    }

    public void setId(Ulid id) {
        this.id = id;
    }

    public Map<Localization, YuGiOhSetTranslationDTO> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<Localization, YuGiOhSetTranslationDTO> translations) {
        this.translations = translations;
    }

    public Ulid getSerieId() {
        return serieId;
    }

    public void setSerieId(Ulid serieId) {
        this.serieId = serieId;
    }

    public boolean isPromo() {
        return promo;
    }

    public void setPromo(boolean promo) {
        this.promo = promo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Ulid getParentId() {
        return parentId;
    }

    public void setParentId(Ulid parentId) {
        this.parentId = parentId;
    }

    public List<OfficialSitePidDTO> getOfficialSitePids() {
        return officialSitePids;
    }

    public void setOfficialSitePids(List<OfficialSitePidDTO> officialSitePids) {
        this.officialSitePids = officialSitePids;
    }

    public List<YugipediaSetDTO> getYugipediaSets() {
        return yugipediaSets;
    }

    public void setYugipediaSets(List<YugipediaSetDTO> yugipediaSets) {
        this.yugipediaSets = yugipediaSets;
    }
}
