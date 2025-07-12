package com.pcagrade.retriever.card.yugioh.source.official;

import com.pcagrade.retriever.card.foil.Foil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public record OfficialSiteRarity(
        String name,
        String code
) {

    private static final List<String> FOIL_RARITIES = List.of(
            "SR",
            "UR",
            "UL",
            "SE",
            "HR",
            "GH",
            "P"
    );

    public static final OfficialSiteRarity COMMON = new OfficialSiteRarity("Common", "C");

    public Foil foil() {
        return FOIL_RARITIES.stream().anyMatch(r -> StringUtils.containsIgnoreCase(code, r)) ? Foil.FOIL_ONLY : Foil.NON_FOIL_ONLY;
    }

    public boolean isCommon() {
        return StringUtils.equalsIgnoreCase(code, "C");
    }

}
