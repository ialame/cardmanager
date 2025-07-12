package com.pcagrade.retriever.card.yugioh.source.ygoprodeck;

import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(YgoProDeckTestConfig.class)
class YgoProDeckParserShould {

    @Autowired
    YgoProDeckParser ygoProDeckParser;

    @Test
    void getSets() {
        var sets = ygoProDeckParser.getSets();

        assertThat(sets).isNotEmpty().allSatisfy(set -> {
            assertThat(set.setCode()).isNotBlank();
            assertThat(set.setName()).isNotBlank();
            assertThat(set.cardCount()).isPositive();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"IOC", "SD09", "KICO"})
    void getCards(String code) {
        var cards = ygoProDeckParser.getCards(code, Localization.USA);

        assertThat(cards).isNotEmpty().allSatisfy(card -> {
            assertThat(card).isNotNull();
            assertThat(card.name()).isNotBlank();
            assertThat(card.cardSets()).isNotEmpty();
        });
    }

}
