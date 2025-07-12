package com.pcagrade.retriever.card.lorcana.source.mushu;

import com.pcagrade.mason.localization.Localization;

import java.util.Map;

public record MushuCard(
        Map<Localization, MushuCardTranslation> translations,
        String ink,
        String set,
        String rarity,
        String artist,
        boolean reprint
) { }
