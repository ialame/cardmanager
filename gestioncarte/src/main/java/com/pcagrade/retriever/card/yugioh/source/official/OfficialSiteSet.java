package com.pcagrade.retriever.card.yugioh.source.official;

import com.pcagrade.mason.localization.Localization;

public record OfficialSiteSet(
        Localization localization,
        String name,
        String pid,
        String type,
        String serie,
        boolean promo
) { }
