package com.pcagrade.retriever.card.pokemon.source.pkmncards;

import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.PokemonCardTestProvider;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestProvider;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(PkmncardsComTestConfig.class)
class PkmncardsComParserShould {

    @Autowired
    private PkmncardsComParser pkmncardsComParser;

    @ParameterizedTest
    @MethodSource("provideValidCardsForArtist")
    void getCardArtist(PokemonSetDTO set, PokemonCardDTO card, String expectedArtist) {
        var name = pkmncardsComParser.getCardArtist(set, card);

        assertThat(name).isEqualTo(expectedArtist);
    }

    @ParameterizedTest
    @MethodSource("provideValidCardsForName")
    void getName(PokemonSetDTO set, PokemonCardDTO card, String expectedArtist) {
        var name = pkmncardsComParser.getName(set, card);

        assertThat(name).isEqualTo(expectedArtist);
    }

    private static Stream<Arguments> provideValidCardsForArtist() {
        return Stream.of(
                Arguments.of(PokemonSetTestProvider.swshDTO(), PokemonCardTestProvider.grookeyDTO(), "kirisAki"),
                Arguments.of(PokemonSetTestProvider.xy1(), PokemonCardTestProvider.venusaurEX(), "Eske Yoshinob"),
                Arguments.of(PokemonSetTestProvider.latiasHalfDeckDTO(), PokemonCardTestProvider.latias(), "Masakazu Fukuda")
        );
    }

    private static Stream<Arguments> provideValidCardsForName() {
        return Stream.of(
                Arguments.of(PokemonSetTestProvider.swshDTO(), PokemonCardTestProvider.grookeyDTO(), "Grookey"),
                Arguments.of(PokemonSetTestProvider.xy1(), PokemonCardTestProvider.venusaurEX(), "Venusaur-EX"),
                Arguments.of(PokemonSetTestProvider.latiasHalfDeckDTO(), PokemonCardTestProvider.latias(), "Latias"),
                Arguments.of(PokemonSetTestProvider.latiasHalfDeckDTO(), PokemonCardTestProvider.latiasHDGrassEnergy(), "Grass Energy")

        );
    }
}
