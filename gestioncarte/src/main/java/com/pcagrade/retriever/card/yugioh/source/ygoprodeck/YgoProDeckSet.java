package com.pcagrade.retriever.card.yugioh.source.ygoprodeck;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record YgoProDeckSet(
        @JsonAlias("set_name") String setName,
        @JsonAlias("set_code") String setCode,
        @JsonAlias("set_rarity") String setRarity,
        @JsonAlias("set_rarity_code") String setRarityCode,
        @JsonAlias("set_price") String setPrice,
        @JsonAlias("num_of_cards") int cardCount,
        @JsonAlias("tcg_date") LocalDate releaseDate
) { }
