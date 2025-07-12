package com.pcagrade.retriever.card.yugioh.source.official;

import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(OfficialSiteTestConfig.class)
class YuGiOhOfficialSiteParserShould {

    @Autowired
    YuGiOhOfficialSiteParser yuGiOhOfficialSiteParser;

    @Test
    void getAllSets() {
        var sets = yuGiOhOfficialSiteParser.getAllSets();

        assertThat(sets).isNotEmpty().allSatisfy(set -> {
            assertThat(set.pid()).isNotBlank();
            assertThat(set.localization()).isNotNull();
        });
    }

    @ParameterizedTest
    @MethodSource("provideValidPids")
    void getCards(String pid, Localization localization, int size) {
        var cards = yuGiOhOfficialSiteParser.getCards(pid, localization);

        assertThat(cards).isNotEmpty().hasSize(size).allSatisfy(card -> {
            assertThat(card.name()).isNotBlank();
            assertThat(card.localization()).isEqualTo(localization);
            assertThat(card.number()).isNotBlank();
        });
    }

    public Stream<Arguments> provideValidPids() {
        return Stream.of(
                Arguments.of("12217000", Localization.USA, 4),
                Arguments.of("2000001188000", Localization.USA, 1),
                Arguments.of("2000001122000", Localization.USA, 57),
                Arguments.of("2201004", Localization.JAPAN, 56)
        );
    }
}
