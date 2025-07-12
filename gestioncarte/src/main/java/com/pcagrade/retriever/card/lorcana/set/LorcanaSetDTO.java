package com.pcagrade.retriever.card.lorcana.set;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.localization.LocalizationUtils;
import com.pcagrade.mason.ulid.UlidHelper;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.lorcana.set.translation.LorcanaSetTranslationDTO;
import com.pcagrade.retriever.card.lorcana.source.mushu.set.MushuSetDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class LorcanaSetDTO {

    public static final Comparator<LorcanaSetDTO> CHANGES_COMPARATOR = Comparator.comparing(LorcanaSetDTO::getSerieId, UlidHelper::compare)
            .thenComparing(LorcanaSetDTO::isPromo)
            .thenComparing(LorcanaSetDTO::getNumber, StringUtils::compare)
            .thenComparing((c1, c2) -> LocalizationUtils.translationComparator(LorcanaSetTranslationDTO.CHANGES_COMPARATOR).compare(c1.getTranslations(), c2.getTranslations()))
            .thenComparing(LorcanaSetDTO::getMushuSets, PCAUtils.collectionComparator(MushuSetDTO.CHANGES_COMPARATOR));

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Ulid id;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<Localization, LorcanaSetTranslationDTO> translations = new EnumMap<>(Localization.class);
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Ulid serieId;
    private String number;
    private boolean promo;
    private List<MushuSetDTO> mushuSets = new ArrayList<>();

    public Ulid getId() {
        return id;
    }

    public void setId(Ulid id) {
        this.id = id;
    }

    public Map<Localization, LorcanaSetTranslationDTO> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<Localization, LorcanaSetTranslationDTO> translations) {
        this.translations = translations;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public List<MushuSetDTO> getMushuSets() {
        return mushuSets;
    }

    public void setMushuSets(List<MushuSetDTO> mushuSets) {
        this.mushuSets = mushuSets;
    }
}
