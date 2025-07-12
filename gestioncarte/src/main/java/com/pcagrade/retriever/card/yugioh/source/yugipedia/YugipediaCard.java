package com.pcagrade.retriever.card.yugioh.source.yugipedia;

import com.pcagrade.mason.localization.Localization;

// https://yugipedia.com/wiki/Template:Set_list
public record YugipediaCard(
        Localization localization,
        String number,
        String name,
        String rarity
) { }
