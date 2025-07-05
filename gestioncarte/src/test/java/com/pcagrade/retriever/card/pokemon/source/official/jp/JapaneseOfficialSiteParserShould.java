package com.pcagrade.retriever.card.pokemon.source.official.jp;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.TestUlidProvider;
import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestProvider;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(JapaneseOfficialSiteTestConfig.class)
class JapaneseOfficialSiteParserShould {

    @Autowired
    private JapaneseOfficialSiteParser japaneseOfficialSiteParser;

    @ParameterizedTest
    @MethodSource("provideSetIds")
    void parse(Ulid id, int size) {
        var map = japaneseOfficialSiteParser.parse(id);

        assertThat(map).hasSize(size);
    }

    private static Stream<Arguments> provideSetIds() {
        return Stream.of(
                Arguments.of(TestUlidProvider.ID_1, 127),
                Arguments.of(PokemonSetTestProvider.POKEMON_GO_JAPAN_ID, 94),
                Arguments.of(PokemonSetTestProvider.LUCARIO_VSTAR_STARTER_SET_ID, 22)
        );
    }

}
