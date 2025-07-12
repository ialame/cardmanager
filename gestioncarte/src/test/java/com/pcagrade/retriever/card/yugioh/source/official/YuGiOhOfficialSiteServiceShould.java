package com.pcagrade.retriever.card.yugioh.source.official;

import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetDTO;
import com.pcagrade.retriever.card.yugioh.source.official.pid.OfficialSitePidDTO;
import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(OfficialSiteTestConfig.class)
class YuGiOhOfficialSiteServiceShould {

    @Autowired
    YuGiOhOfficialSiteService yuGiOhOfficialSiteService;

    @Test
    void getAllSets() {
        var sets = yuGiOhOfficialSiteService.getAllSets();

        assertThat(sets).isNotEmpty().allSatisfy(set -> assertThat(set.getValue().getOfficialSitePids()).isNotEmpty());
    }

    @Test
    void getSerieWithPid() {
        var opt = yuGiOhOfficialSiteService.getSerieWithPid("2000001225000", Localization.USA);

        assertThat(opt).isNotEmpty().hasValueSatisfying(serie -> assertThat(serie.getTranslations().get(Localization.USA).getName()).isEqualTo("2023"));
    }

    @Test
    void extractCardsFromSet() {
        var set = new YuGiOhSetDTO();

        set.setOfficialSitePids(List.of(
                new OfficialSitePidDTO("2000001225000", Localization.USA),
                new OfficialSitePidDTO("2000001225000", Localization.FRANCE)
        ));

        var cards = yuGiOhOfficialSiteService.extractCards(set, null);

        assertThat(cards).isNotEmpty()
                .hasSize(75)
                .allSatisfy(card -> {
                    assertThat(card).isNotNull();
                    assertThat(card.getValue()).isNotNull();
                    assertThat(card.getValue().getTranslations())
                            .isNotEmpty()
                            .containsKeys(Localization.USA, Localization.FRANCE);
                });
    }

    @ParameterizedTest
    @MethodSource("provide_setThatShouldBeGrouped")
    void shouldGroupSets_should_returnTrue(OfficialSiteSet set1, OfficialSiteSet set2) {
        assertThat(yuGiOhOfficialSiteService.shouldGroupSets(set1, set2)).isTrue();
    }


    private static Stream<Arguments> provide_setThatShouldBeGrouped() {
        return Stream.of(
                Arguments.of(
                        new OfficialSiteSet(Localization.USA, "name", "1", "type", "serie", false),
                        new OfficialSiteSet(Localization.USA, "name", "1", "type", "serie", false)
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provide_setThatShouldNotBeGrouped")
    void shouldGroupSets_shouldNot_returnTrue(OfficialSiteSet set1, OfficialSiteSet set2) {
        assertThat(yuGiOhOfficialSiteService.shouldGroupSets(set1, set2)).isFalse();
    }

    private static Stream<Arguments> provide_setThatShouldNotBeGrouped() {
        return Stream.of(
                Arguments.of(
                        new OfficialSiteSet(Localization.USA, "name1", "1", "type", "serie", false),
                        new OfficialSiteSet(Localization.USA, "name2", "2", "type", "serie", false)
                ),
                Arguments.of(
                        new OfficialSiteSet(Localization.GERMANY, "Yu-Gi-Oh! Adventskalender", "28804001", "Sonstiges", "2011", false),
                        new OfficialSiteSet(Localization.GERMANY, "Yu-Gi-Oh! Adventskalender", "18812003", "Sonstiges", "2018", false)
                )
        );
    }
}
