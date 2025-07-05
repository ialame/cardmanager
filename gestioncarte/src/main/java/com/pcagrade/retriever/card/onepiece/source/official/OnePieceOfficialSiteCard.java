package com.pcagrade.retriever.card.onepiece.source.official;

import com.pcagrade.mason.localization.Localization;

import java.util.List;

public record OnePieceOfficialSiteCard(
        String name,
        Localization localization,
        String number,
        String type,
        String rarity,
        int parallel,
        String attribute,
        String color,
        List<String> distributions
) {
}
