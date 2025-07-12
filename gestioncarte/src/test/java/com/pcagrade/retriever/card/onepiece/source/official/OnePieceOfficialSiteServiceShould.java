package com.pcagrade.retriever.card.onepiece.source.official;

import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(OnePieceOfficialSiteTestConfig.class)
class OnePieceOfficialSiteServiceShould {

    @Autowired
    OnePieceOfficialSiteService onePieceOfficialSiteService;

    @Test
    void getSeries() {
        var series = onePieceOfficialSiteService.getSeries();

        assertThat(series).isNotEmpty()
                .allSatisfy(s -> assertThat(s.getTranslations()).isNotEmpty()
                        .allSatisfy((k, v) -> {
                            assertThat(k).isNotNull();
                            assertThat(v.getName()).isNotBlank();
                        }));
    }

    @Test
    void findSerieByCode() {
        var serie = onePieceOfficialSiteService.findSerieByCode("ST-07");

        assertThat(serie)
                .isNotEmpty()
                .hasValueSatisfying(s -> assertThat(s.getTranslations()).containsKeys(Localization.JAPAN, Localization.USA));
    }

    @Test
    void findPromoSet() {
        var serie = onePieceOfficialSiteService.findSetByCode("P");

        assertThat(serie)
                .isNotEmpty()
                .hasValueSatisfying(s -> assertThat(s.getTranslations()).containsKeys(Localization.JAPAN, Localization.USA));
    }

    @Test
    void findLPCSet() {
        var serie = onePieceOfficialSiteService.findSetByCode("LPC");

        assertThat(serie)
                .isNotEmpty()
                .hasValueSatisfying(s -> assertThat(s.getTranslations()).containsKeys(Localization.JAPAN));
    }

    @Test
    void getSets() {
        var sets = onePieceOfficialSiteService.getSets();

        assertThat(sets).isNotEmpty()
                .allSatisfy(s -> {
                    assertThat(s.getOfficialSiteIds()).isNotEmpty();
                    assertThat(s.getTranslations()).isNotEmpty()
                            .containsKey(Localization.JAPAN)
                            .allSatisfy((k, v) -> {
                                assertThat(k).isNotNull();
                                assertThat(v.getName()).isNotBlank();
                            });
                });
    }
}
