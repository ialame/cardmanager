package com.pcagrade.retriever.card.pokemon.source.official.jp;

public record JapaneseOfficialSiteCard(
        String id,
        String url,
        String number,
        String name,
        String artist,
        String trainer
) {
    public static final JapaneseOfficialSiteCard DEFAULT = new JapaneseOfficialSiteCard("", "", "", "", "", "");
}
