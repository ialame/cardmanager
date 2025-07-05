package com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction;

import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.pokemon.PokemonCardHelper;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestProvider;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.expansion.ExpansionBulbapediaDTO;
import com.pcagrade.retriever.card.tag.CardTagTestProvider;
import com.pcagrade.retriever.card.tag.type.CardTagType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(BulbapediaExtractionServiceTestConfig.class)
class BulbapediaExtractionServiceShould {

    @Autowired
    private BulbapediaExtractionService bulbapediaExtractionService;

    @ParameterizedTest
    @MethodSource("provideValidExtensions")
    void getCardsFromBulbapedia_with_validId(int expansionId, boolean distribution, String filter) {
        var list = bulbapediaExtractionService.getCardsFromBulbapedia(expansionId, distribution, List.of(filter), null);

        assertThat(list).isNotEmpty()
                .allMatch(c -> c.status() == BulbapediaExtractionStatus.OK, "has status OK")
                .allSatisfy(c -> assertThat(c.card().getTranslations()).allSatisfy((l, t) -> {
                    isValidName(t.getName());
                    isValidName(t.getLabelName());
                })).allSatisfy(c -> assertThat(c.card().getPromos()).noneSatisfy(p -> assertThat(p.getName()).containsIgnoringCase("  ")));

        if (distribution) {
            assertThat(list)
                    .allSatisfy(c -> assertThat(c.card().getTranslations()).hasSizeGreaterThanOrEqualTo(1))
                    .allSatisfy(c -> assertThat(c.card().getSetIds()).hasSizeGreaterThanOrEqualTo(1));
        } else {
            assertThat(list)
                    .allSatisfy(c -> assertThat(c.card().getTranslations()).hasSize(2))
                    .allSatisfy(c -> assertThat(c.card().getSetIds()).hasSize(2));

        }
    }

    @ParameterizedTest
    @MethodSource("provideValidExtensionsWithPromo")
    void getCardsFromBulbapedia_with_promo_multiple_cards(int id, String filter) {
        var list = bulbapediaExtractionService.getCardsFromBulbapedia(id, false, List.of(filter), null);

        assertThat(list).isNotEmpty()
                .allMatch(c -> c.status() == BulbapediaExtractionStatus.OK, "has status OK")
                .allSatisfy(c -> assertThat(c.card().getTranslations()).allSatisfy((l, t) -> {
                    isValidName(t.getName());
                    isValidName(t.getLabelName());
                }))
                .allSatisfy(c -> assertThat(c.card().getPromos()).noneSatisfy(p -> assertThat(p.getName()).containsIgnoringCase("  ")))
                .size().isGreaterThanOrEqualTo(2);

        assertThat(list.isEmpty() ? null :list.get(0))
                .satisfies(c -> assertThat(c.card().getTranslations()).isNotEmpty())
                .satisfies(c -> assertThat(c.card().getSetIds()).isNotEmpty())
                .satisfies(c -> assertThat(c.card().getBrackets()).isEmpty())
                .satisfies(c -> assertThat(c.card().getPromos())
                        .filteredOn(p -> p.getLocalization() == Localization.USA)
                        .isNotEmpty());
        assertThat(list.subList(1, list.size()))
                .allSatisfy(c -> assertThat(c.card().getTranslations()).hasSize(1))
                .allSatisfy(c -> assertThat(c.card().getSetIds()).hasSize(1))
                .allSatisfy(c -> assertThat(c.card().getBrackets()).isNotEmpty())
                .allSatisfy(c -> assertThat(c.card().getPromos()).isNotEmpty());
    }

    @Test
    void getCardsFromBulbapedia_with_validId_no_set() {
        var list = bulbapediaExtractionService.getCardsFromBulbapedia(489, false, List.of("001/192"), null);

        assertThat(list).isNotEmpty()
                .allMatch(c -> c.status() == BulbapediaExtractionStatus.NO_SET, "has status to not set")
                .allSatisfy(c -> assertThat(c.card().getTranslations()).allSatisfy((l, t) -> {
                    isValidName(t.getName());
                    isValidName(t.getLabelName());
                }));;
        assertThat(list.isEmpty() ? null :list.get(0).card().getTranslations()).hasSize(2);
        assertThat(list.isEmpty() ? null :list.get(0).card().getSetIds()).hasSize(1);
    }

    @Test
    void getCardsFromBulbapedia_with_validId_distribution() {
        var list = bulbapediaExtractionService.getCardsFromBulbapedia(489, true, List.of("020/192"), null);

        assertThat(list).isNotEmpty()
                .allMatch(c -> c.status() == BulbapediaExtractionStatus.OK, "has status OK")
                .allMatch(c -> c.card().isDistribution(), "is distribution")
                .allMatch(c -> !c.card().getPromos().isEmpty(), "has promo")
                .allSatisfy(c -> assertThat(c.card().getTranslations()).allSatisfy((l, t) -> {
                    isValidName(t.getName());
                    isValidName(t.getLabelName());
                }));;
    }

    @Test
    void getCardsFromBulbapedia_with_swsh178() {
        var list = bulbapediaExtractionService.getCardsFromBulbapedia(488, false, List.of("SWSH178"), null);

        assertThat(list).isNotEmpty().hasSize(2)
                .allMatch(c -> c.status() == BulbapediaExtractionStatus.OK, "has status OK")
                .allSatisfy(c -> assertThat(c.card().getTranslations()).allSatisfy((l, t) -> assertThat(t.getTrainer()).isEqualTo("Professor Willow")))
                .allSatisfy(c -> assertThat(c.card().getBrackets()).noneSatisfy(b -> assertThat(b.getName()).isEqualTo("Professor Willow")))
                .allSatisfy(c -> assertThat(c.card().getTranslations()).hasSize(1))
                .allSatisfy(c -> assertThat(c.card().getSetIds()).hasSize(1))
                .allSatisfy(c -> assertThat(c.card().getPromos()).hasSize(1))
                .allSatisfy(c -> assertThat(c.card().getTranslations()).allSatisfy((l, t) -> {
                    isValidName(t.getName());
                    isValidName(t.getLabelName());
                }));

        assertThat(list.get(0)).satisfies(c -> assertThat(c.card().getBrackets()).isEmpty());
        assertThat(list.get(1)).satisfies(c -> assertThat(c.card().getBrackets()).hasSize(1));
    }

    @Test
    void getCardsFromBulbapedia_with_sm190() {
        var list = bulbapediaExtractionService.getCardsFromBulbapedia(463, false, List.of("SM190"), null);

        assertThat(list).isNotEmpty().hasSize(2)
                .allMatch(c -> c.status() == BulbapediaExtractionStatus.OK, "has status OK")
                .allSatisfy(c -> assertThat(c.card().getTranslations()).allSatisfy((l, t) -> {
                    isValidName(t.getName());
                    isValidName(t.getLabelName());
                }));
        assertThat(list.get(0)).satisfies(c -> assertThat(c.card().getBrackets()).hasSize(1).satisfies(b -> assertThat(b.get(0).getName()).isEqualTo("Sheen Holo")));
        assertThat(list.get(1)).satisfies(c -> assertThat(c.card().getBrackets()).hasSize(2)
                .satisfies(b -> {
                    assertThat(b.get(0).getName()).isEqualTo("Water Web Holo");
                    assertThat(b.get(1).getName()).isEqualTo("Detective Pikachu Stamp");
                }));
    }

    @Test
    void getCardsFromBulbapedia_with_196sp() {
        var list = bulbapediaExtractionService.getCardsFromBulbapedia(502, false, List.of("196/S-P"), null);

        assertThat(list).isNotEmpty().hasSize(1);

        var card = list.isEmpty() ? null :list.get(0);

        assertThat(card.status()).isEqualTo(BulbapediaExtractionStatus.NOT_IN_PAGE_2);
        assertThat(card.card().getTranslations()).hasSize(1);
        assertThat(card.card().getTranslations().get(Localization.JAPAN).getName()).isEqualTo("Jirachi {0}");
    }

    @Test
    void getCardsFromBulbapedia_with_masterKitSideDeck() {
        var list = bulbapediaExtractionService.getCardsFromBulbapedia(122, false, List.of("001/012"), null);

        assertThat(list).isNotEmpty().hasSize(1)
                .allMatch(c -> c.status() == BulbapediaExtractionStatus.OK, "has status OK")
                .allSatisfy(c -> assertThat(c.card().getTranslations()).allSatisfy((l, t) -> {
                    isValidName(t.getName());
                    isValidName(t.getLabelName());
                }));
    }

    @Test
    void getCardsFromBulbapedia_with_quaxlyAndMimikyuEX() {
        var list = bulbapediaExtractionService.getCardsFromBulbapedia(621, false, List.of("003/023"), null);

        assertThat(list).isNotEmpty().hasSize(1)
                .allMatch(c -> c.status() == BulbapediaExtractionStatus.OK, "has status OK")
                .allSatisfy(c -> assertThat(c.card().getTranslations())
                        .containsKey(Localization.JAPAN)
                        .allSatisfy((l, t) -> {
                            isValidName(t.getName());
                            isValidName(t.getLabelName());
                        }));
    }

    @Test
    void getCardsFromBulbapedia_with_forme() {
        var list = bulbapediaExtractionService.getCardsFromBulbapedia(404, false, List.of("1/146"), null);

        assertThat(list).isNotEmpty().hasSize(1)
                .allMatch(c -> c.status() == BulbapediaExtractionStatus.OK, "has status OK")
                .allSatisfy(c -> assertThat(c.card().getBrackets()).isEmpty())
                .allSatisfy(c -> assertThat(c.card().getTranslations()).hasSize(2))
                .allSatisfy(c -> assertThat(c.card().getSetIds()).hasSize(2))
                .allSatisfy(c -> assertThat(c.card().getTags()).hasSize(1)
                        .allSatisfy(t -> assertThat(t.getType()).isEqualTo(CardTagType.FORME))
                        .allSatisfy(t -> assertThat(t.getTranslations().get(Localization.USA).getName()).isEqualTo("Normal Forme")))
                .allSatisfy(c -> assertThat(c.card().getTranslations()).allSatisfy((l, t) -> {
                    isValidName(t.getName());
                    isValidName(t.getLabelName());
                }));
    }

    @Test
    void getCardsFromBulbapedia_with_regional_form() {
        var list = bulbapediaExtractionService.getCardsFromBulbapedia(489, false, List.of("126/192"), null);

        assertThat(list).isNotEmpty().hasSize(1)
                .allMatch(c -> c.status() == BulbapediaExtractionStatus.OK, "has status OK")
                .allSatisfy(c -> assertThat(c.card().getBrackets()).isEmpty())
                .allSatisfy(c -> assertThat(c.card().getTranslations()).hasSize(2))
                .allSatisfy(c -> assertThat(c.card().getSetIds()).hasSize(2))
                .allSatisfy(c -> assertThat(c.card().getTags()).hasSize(1)
                        .allSatisfy(t -> assertThat(t.getType()).isEqualTo(CardTagType.REGIONAL_FORM))
                        .allSatisfy(t -> assertThat(t.getTranslations().get(Localization.USA).getName()).isEqualTo("Galar")))
                .allSatisfy(c -> assertThat(c.card().getTranslations()).allSatisfy((l, t) -> {
                    isValidName(t.getName());
                    isValidName(t.getLabelName());
                }));
    }

    @Test
    void getCardsFromBulbapedia_with_formeFromBracket() {
        var list = bulbapediaExtractionService.getCardsFromBulbapedia(90, false, List.of("017/PCG-P"), null);

        assertThat(list).isNotEmpty().hasSize(1)
                .allMatch(c -> c.status() == BulbapediaExtractionStatus.OK, "has status OK")
                .allSatisfy(c -> assertThat(c.card().getBrackets()).isEmpty())
                .allSatisfy(c -> assertThat(c.card().getTranslations()).hasSize(2))
                .allSatisfy(c -> assertThat(c.card().getSetIds()).hasSize(2))
                .allSatisfy(c -> assertThat(c.card().getTags()).hasSize(1)
                        .allSatisfy(t -> assertThat(t.getType()).isEqualTo(CardTagType.FORME))
                        .allSatisfy(t -> assertThat(t.getTranslations().get(Localization.USA).getName()).isEqualTo("Defense Forme")))
                .allSatisfy(c -> assertThat(c.card().getTranslations()).allSatisfy((l, t) -> {
                    isValidName(t.getName());
                    isValidName(t.getLabelName());
                }));
    }

    @Test
    void getCardsFromBulbapedia_with_newFormeFromBracket() {
        var list = bulbapediaExtractionService.getCardsFromBulbapedia(90, false, List.of("011/PCG-P"), null);

        assertThat(list).isNotEmpty().hasSize(1)
                .allMatch(c -> c.status() == BulbapediaExtractionStatus.OK, "has status OK")
                .allSatisfy(c -> assertThat(c.card().getBrackets()).isEmpty())
                .allSatisfy(c -> assertThat(c.card().getTranslations()).hasSize(2))
                .allSatisfy(c -> assertThat(c.card().getSetIds()).hasSize(2))
                .allSatisfy(c -> assertThat(c.card().getTags()).hasSize(1)
                        .allSatisfy(t -> assertThat(t.getType()).isEqualTo(CardTagType.FORME))
                        .allSatisfy(t -> assertThat(t.getTranslations().get(Localization.USA).getName()).isEqualTo("Normal Forme")))
                .allSatisfy(c -> assertThat(c.card().getTranslations()).allSatisfy((l, t) -> {
                    isValidName(t.getName());
                    isValidName(t.getLabelName());
                }));
    }

    @Test
    void getCardsFromBulbapedia_with_galarRegionalForm() {
        var list = bulbapediaExtractionService.getCardsFromBulbapedia(489, false, List.of("126/192"), null);

        assertThat(list).isNotEmpty().hasSize(1)
                .allMatch(c -> c.status() == BulbapediaExtractionStatus.OK, "has status OK")
                .allSatisfy(c -> assertThat(c.card().getBrackets()).isEmpty())
                .allSatisfy(c -> assertThat(c.card().getTranslations()).hasSize(2))
                .allSatisfy(c -> assertThat(c.card().getSetIds()).hasSize(2))
                .allSatisfy(c -> assertThat(c.card().getTags()).hasSize(1).allSatisfy(t -> {
                        assertThat(t.getType()).isEqualTo(CardTagType.REGIONAL_FORM);
                        assertThat(t.getId()).isEqualTo(CardTagTestProvider.GALAR_ID);
                        assertThat(t.getTranslations().get(Localization.USA).getName()).isEqualTo("Galar");
                    }))
                .allSatisfy(c -> assertThat(c.card().getTranslations()).allSatisfy((l, t) -> {
                    isValidName(t.getName());
                    isValidName(t.getLabelName());
                }));
    }

    @ParameterizedTest
    @MethodSource("provideValidExtensionsWithUnnumberedJp")
    void getCardsFromBulbapedia_with_unnumbered_jp(int expansionId, String filter) {
        var list = bulbapediaExtractionService.getCardsFromBulbapedia(expansionId, false, List.of(filter), null);

        assertThat(list).isNotEmpty()
                .allMatch(c -> !c.card().getPromos().isEmpty(), "has promo")
                .allSatisfy(c -> assertThat(c.card().getTranslations()).allSatisfy((l, t) -> {
                    isValidName(t.getName());
                    isValidName(t.getLabelName());
                }));;
        assertThat(list.isEmpty() ? null :list.get(0))
                .matches(c -> c.status() == BulbapediaExtractionStatus.NOT_IN_PAGE_1, "has status not in page 1")
                .matches(c -> c.card().getTranslations().size() == 2, "has 2 translation")
                .matches(c -> c.card().getSetIds().size() == 1, "has 1 set id")
                .matches(c -> PokemonCardHelper.NO_NUMBER.equals(c.card().getTranslations().get(Localization.JAPAN).getNumber()), "has no number")
                .satisfies(c -> assertThat(c.card().getPromos()).isNotEmpty()
                        .allMatch(p -> p.getLocalization() != Localization.JAPAN, "has no promo in japan"));
    }

    @ParameterizedTest
    @ValueSource(ints = { 631, 632 })
    void getCardsFromBulbapedia_with_iconInNumber(int id) {
        var list = bulbapediaExtractionService.getCardsFromBulbapedia(id, false, Collections.emptyList(), null);

        assertThat(list).isNotEmpty()
                .allMatch(c -> c.status() == BulbapediaExtractionStatus.NOT_IN_PAGE_2, "has status not in page 2")
                .allSatisfy(c -> assertThat(c.card().getTranslations()).allSatisfy((l, t) -> {
                    isValidName(t.getName());
                    isValidName(t.getLabelName());
                }))
                .allSatisfy(c -> assertThat(c.card().getTranslations()).hasSize(1))
                .allSatisfy(c -> assertThat(c.card().getSetIds()).hasSize(1));
    }

    @Test
    void return_fullCount_with_no_filter() {
        var count = bulbapediaExtractionService.getCardCountFromBulbapedia(PokemonSetTestProvider.REBEL_CLASH_ID, false, Collections.emptyList());

        assertThat(count).isEqualTo(209);
    }

    @Test
    void return_1_with_1_filter() {
        var count = bulbapediaExtractionService.getCardCountFromBulbapedia(PokemonSetTestProvider.REBEL_CLASH_ID, false, List.of("001/192"));

        assertThat(count).isEqualTo(1);
    }

    @Test
    void return_2_with_2_filter() {
        var count = bulbapediaExtractionService.getCardCountFromBulbapedia(PokemonSetTestProvider.REBEL_CLASH_ID, false, List.of("001/192", "002/192"));

        assertThat(count).isEqualTo(2);
    }

    @Test
    void return_empty_with_emptyList() {
        var images = bulbapediaExtractionService.findImagesForExpansions(List.of());

        assertThat(images).isEmpty();
    }

    @Test
    void return_1image_with_validExtension() {
        var extension = new ExpansionBulbapediaDTO();

        extension.setId(1000);
        extension.setLocalization(Localization.USA);
        extension.setUrl("https://bulbapedia.bulbagarden.net/wiki/SWSH_Black_Star_Promos_(TCG)");

        var images = bulbapediaExtractionService.findImagesForExpansions(List.of(extension));

        assertThat(images).hasSize(1);
    }

    private static void isValidName(String t) {
        assertThat(t)
                .doesNotContainIgnoringCase("<small>")
                .doesNotContainIgnoringCase("</small>")
                .doesNotContainIgnoringCase("<br>");
    }

    private static Stream<Arguments> provideValidExtensions() {
        return Stream.of(
                Arguments.of(247, false, "058/060"),
                Arguments.of(554, false, "001/078"),
                Arguments.of(555, false, "001/071"),
                Arguments.of(473, true, "188a/214"),
                Arguments.of(364, false, "095/095"),
                Arguments.of(365, false, "130/173"),
                Arguments.of(478, true, "182b/214"),
                Arguments.of(540, false, "TG01/TG30"),
                Arguments.of(406, false, "116/127"),
                Arguments.of(576, false, "001/013"),
                Arguments.of(334, false, "337/SM-P"),
                Arguments.of(463, false, "SM103a")
                // TODO Arguments.of(567, false, "052/068")
        );
    }

    private static Stream<Arguments> provideValidExtensionsWithPromo() {
        return Stream.of(
                Arguments.of(463, "SM03"),
                Arguments.of(463, "SM04"),
                Arguments.of(463, "SM65"),
                Arguments.of(463, "SM86"),
                Arguments.of(463, "SM127"),
                Arguments.of(488, "SWSH178")
        );
    }

    private static Stream<Arguments> provideValidExtensionsWithUnnumberedJp() {
        return Stream.of(
                Arguments.of(444, "DP05")
        );
    }
}
