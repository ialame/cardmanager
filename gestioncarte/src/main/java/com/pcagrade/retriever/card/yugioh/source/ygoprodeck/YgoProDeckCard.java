package com.pcagrade.retriever.card.yugioh.source.ygoprodeck;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record YgoProDeckCard(
        String name,
        String type,
        String race,
        @JsonAlias("card_sets") List<YgoProDeckSet> cardSets
) { }
