package com.pcagrade.retriever.card.yugioh.source.official;

import com.pcagrade.retriever.card.foil.Foil;
import com.pcagrade.mason.localization.Localization;

import java.time.LocalDate;
import java.util.List;

public record OfficialSiteCard(
        String cid,
        Localization localization,
        String number,
        String name,
        OfficialSiteRarity rarity,
        LocalDate releaseDate,
        boolean sneakPeek,
        List<String> types
) {

    public Foil foil() {
        return rarity.foil();
    }
}
