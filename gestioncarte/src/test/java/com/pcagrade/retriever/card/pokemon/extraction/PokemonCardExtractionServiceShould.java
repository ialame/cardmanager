package com.pcagrade.retriever.card.pokemon.extraction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcagrade.retriever.TestUlidProvider;
import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.extraction.history.CardExtractionHistoryRepository;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.PokemonCardServiceTestConfig;
import com.pcagrade.retriever.card.pokemon.PokemonCardTestUtils;
import com.pcagrade.retriever.card.pokemon.extraction.history.PokemonCardExtractionHistoryService;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestConfig;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestProvider;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction.BulbapediaExtractionServiceTestConfig;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction.BulbapediaExtractionStatus;
import com.pcagrade.retriever.card.pokemon.source.pkmncards.PkmncardsComTestConfig;
import com.pcagrade.retriever.card.tag.CardTagTestProvider;
import com.pcagrade.retriever.card.tag.type.CardTagType;
import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(PokemonCardExtractionServiceShould.Config.class)
class PokemonCardExtractionServiceShould {

    @Autowired
    PokemonCardExtractionService pokemonCardExtractionService;

    @Test
    void extract_simple_japan_set() {
        var cards = pokemonCardExtractionService.extractPokemonCards(PokemonSetTestProvider.COLLECTION_X_ID, false, Collections.emptyList());

        assertThat(cards)
                .isNotEmpty()
                .map(ExtractedPokemonCardDTO::getCard)
                .allMatch(c -> c.getTranslations().containsKey(Localization.JAPAN))
                .flatMap(c -> c.getTranslations().values())
                .are(PokemonCardTestUtils.WITHOUT_INVALID_FEATURES);
        assertThat(getCard(cards, Localization.JAPAN, "001/060").getTranslations().get(Localization.FRANCE)).isNotNull()
                .hasFieldOrPropertyWithValue("name", "Florizarre EX");
    }

    @Test
    void extract_distribution() {
        var cards = pokemonCardExtractionService.extractPokemonCards(PokemonSetTestProvider.REBEL_CLASH_ID, true, Collections.emptyList());

        assertThat(cards)
                .isNotEmpty()
                .map(ExtractedPokemonCardDTO::getCard)
                .allMatch(c -> c.getTranslations().containsKey(Localization.USA))
                .allMatch(PokemonCardDTO::isDistribution);
        assertThat(getCard(cards, Localization.USA, "020/192")).isNotNull()
                .satisfies(c -> assertThat(c.getParentId()).isEqualTo(TestUlidProvider.ID_2))
                .hasFieldOrPropertyWithValue("fullArt", true);
    }

    @Test
    void extract_with_galarRegionalForm() {
        var cards = pokemonCardExtractionService.extractPokemonCards(PokemonSetTestProvider.REBEL_CLASH_ID, false, List.of("126/192"));

        assertThat(cards).isNotEmpty().hasSize(1)
                .allSatisfy(c -> assertThat(c.getBulbapediaStatus()).isEqualTo(BulbapediaExtractionStatus.OK))
                .allSatisfy(c -> assertThat(c.getCard().getBrackets()).isEmpty())
                .allSatisfy(c -> assertThat(c.getCard().getTranslations()).hasSize(2))
                .allSatisfy(c -> assertThat(c.getCard().getSetIds()).hasSize(2))
                .allSatisfy(c -> assertThat(c.getCard().getTags()).hasSize(1).allSatisfy(t -> {
                    assertThat(t.getType()).isEqualTo(CardTagType.REGIONAL_FORM);
                    assertThat(t.getId()).isEqualTo(CardTagTestProvider.GALAR_ID);
                    assertThat(t.getTranslations().get(Localization.USA).getName()).isEqualTo("Galar");
                }));
    }

    @Test
    void return_fullCount_with_no_filter() {
        var count = pokemonCardExtractionService.countPokemonCardsToExtract(PokemonSetTestProvider.REBEL_CLASH_ID, false, Collections.emptyList());

        assertThat(count).isEqualTo(209);
    }

    @Test
    void return_1_with_1_filter() {
        var count = pokemonCardExtractionService.countPokemonCardsToExtract(PokemonSetTestProvider.REBEL_CLASH_ID, false, List.of("001/192"));

        assertThat(count).isEqualTo(1);
    }

    @Test
    void return_2_with_2_filter() {
        var count = pokemonCardExtractionService.countPokemonCardsToExtract(PokemonSetTestProvider.REBEL_CLASH_ID, false, List.of("001/192", "002/192"));

        assertThat(count).isEqualTo(2);
    }

    @Test
    void extractPokemonCards_with_quaxlyAndMimikyuEX() {
        var list = pokemonCardExtractionService.extractPokemonCards(PokemonSetTestProvider.QUAXLY_AND_MIMIKYU_EX_ID, false, Collections.emptyList());

        assertThat(list).hasSize(23)
                .allSatisfy(c -> assertThat(c.getCard().getTranslations()).containsKey(Localization.JAPAN));
    }

    private PokemonCardDTO getCard(List<ExtractedPokemonCardDTO> cards, Localization localization, String number) {
        return cards.stream()
                .filter(c -> number.equals(c.getCard().getTranslations().get(localization).getNumber()))
                .findFirst()
                .orElseThrow()
                .getCard();
    }

    @RetrieverTestConfiguration
    @Import({ BulbapediaExtractionServiceTestConfig.class, PokemonSetTestConfig.class, PokemonCardServiceTestConfig.class, PkmncardsComTestConfig.class })
    public static class Config {

        @Bean
        public CardExtractionHistoryRepository cardExtractionHistoryRepository() {
            return Mockito.mock(CardExtractionHistoryRepository.class);
        }

        @Bean
        public PokemonCardExtractionHistoryService pokemonCardExtractionHistoryService() {
            return new PokemonCardExtractionHistoryService();
        }

        @Bean
        public PokemonCardExtractionService pokemonCardExtractionService(ObjectMapper objectMapper) {
            return new PokemonCardExtractionService(objectMapper);
        }

    }
}
