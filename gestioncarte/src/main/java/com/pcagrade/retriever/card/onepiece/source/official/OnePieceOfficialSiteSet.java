package com.pcagrade.retriever.card.onepiece.source.official;

import com.pcagrade.mason.localization.Localization;
import org.apache.commons.lang3.StringUtils;

public record OnePieceOfficialSiteSet(
        int id,
        Localization localization,
        String name,
        String code,
        String serie
) {

    public boolean isPromo() {
        return StringUtils.equalsIgnoreCase(serie, "promo");
    }

}
