package com.pcagrade.retriever.card.pokemon.source.official;

import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.pokemon.PokemonCardHelper;
import com.pcagrade.retriever.card.pokemon.PokemonCardTestProvider;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestProvider;
import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(OfficialSiteTestConfig.class)
@Disabled
class OfficialSiteParserShould {

    @Autowired
    OfficialSiteParser officialSiteParser;

    @Test
    void getCardName() {
        var name = officialSiteParser.getCardName(PokemonSetTestProvider.xy1(), PokemonCardTestProvider.venusaurEX(), Localization.USA);

        assertThat(name).isNotBlank()
                .isEqualToIgnoringCase("Venusaur-EX");
    }

    @ParameterizedTest
    @MethodSource("provideValidUrls")
    void getCardName_with_valid_urls(String url, Localization localization, String name) {
        var value = officialSiteParser.getCardName(url, localization);

        assertThat(value).isNotBlank()
                .isEqualToIgnoringCase(name);
    }

    @ParameterizedTest
    @MethodSource("provideValidUrlsWithTrainer")
    void getCardName_and_trainer_with_valid_urls(String url, Localization localization) {
        var pair = PokemonCardHelper.extractTrainer(officialSiteParser.getCardName(url, localization));

        assertThat(pair.getLeft()).isNotBlank();
        assertThat(pair.getRight()).isNotBlank();
    }


    private static Stream<Arguments> provideValidUrlsWithTrainer() {
        return Stream.of(
                Arguments.of( "https://www.pokemon.com/us/pokemon-tcg/pokemon-cards/ss-series/cel25/23/", Localization.USA),
                Arguments.of( "https://www.pokemon.com/fr/jcc-pokemon/cartes-pokemon/ss-series/cel25/23/", Localization.FRANCE),
                Arguments.of( "https://www.pokemon.com/it/gcc/archivio-carte/ss-series/cel25/23/", Localization.ITALY),
                Arguments.of( "https://www.pokemon.com/de/pokemon-sammelkartenspiel/pokemon-karten/ss-series/cel25/23/", Localization.GERMANY),
                Arguments.of( "https://www.pokemon.com/es/jcc-pokemon/cartas-pokemon/ss-series/cel25/23/", Localization.SPAIN),
                Arguments.of( "https://www.pokemon.com/br/pokemon-estampas-ilustradas/cartas-de-pokemon/ss-series/cel25/23/", Localization.PORTUGAL)
        );
    }

    private static Stream<Arguments> provideValidUrls() {
        return Stream.of(
                Arguments.of("https://www.pokemon.com/us/pokemon-tcg/pokemon-cards/xy-series/xy1/1/", Localization.USA, "Venusaur-EX"),
                Arguments.of("https://www.pokemon.com/es/jcc-pokemon/cartas-pokemon/ss-series/swsh45/48/", Localization.SPAIN, "Thievul"),
                Arguments.of("https://www.pokemon.com/us/pokemon-tcg/pokemon-cards/ss-series/swshp/SWSH139/", Localization.USA, "Pikachu V-UNION"),
                Arguments.of("https://www.pokemon.com/us/pokemon-tcg/pokemon-cards/ss-series/swshp/SWSH140/", Localization.USA, "Pikachu V-UNION"),
                Arguments.of("https://www.pokemon.com/us/pokemon-tcg/pokemon-cards/ss-series/swshp/SWSH141/", Localization.USA, "Pikachu V-UNION"),
                Arguments.of("https://www.pokemon.com/us/pokemon-tcg/pokemon-cards/ss-series/swshp/SWSH142/", Localization.USA, "Pikachu V-UNION"),
                Arguments.of("https://www.pokemon.com/fr/jcc-pokemon/cartes-pokemon/ss-series/swshp/SWSH139/", Localization.FRANCE, "Pikachu V-UNION"),
                Arguments.of("https://www.pokemon.com/fr/jcc-pokemon/cartes-pokemon/ss-series/swshp/SWSH140/", Localization.FRANCE, "Pikachu V-UNION"),
                Arguments.of("https://www.pokemon.com/fr/jcc-pokemon/cartes-pokemon/ss-series/swshp/SWSH141/", Localization.FRANCE, "Pikachu V-UNION"),
                Arguments.of("https://www.pokemon.com/fr/jcc-pokemon/cartes-pokemon/ss-series/swshp/SWSH142/", Localization.FRANCE, "Pikachu V-UNION"),
                Arguments.of("https://www.pokemon.com/it/gcc/archivio-carte/ss-series/swshp/SWSH139/", Localization.ITALY, "Pikachu V UNIONE"),
                Arguments.of("https://www.pokemon.com/it/gcc/archivio-carte/ss-series/swshp/SWSH140/", Localization.ITALY, "Pikachu V UNIONE"),
                Arguments.of("https://www.pokemon.com/it/gcc/archivio-carte/ss-series/swshp/SWSH141/", Localization.ITALY, "Pikachu V UNIONE"),
                Arguments.of("https://www.pokemon.com/it/gcc/archivio-carte/ss-series/swshp/SWSH142/", Localization.ITALY, "Pikachu V UNIONE"),
                Arguments.of("https://www.pokemon.com/de/pokemon-sammelkartenspiel/pokemon-karten/ss-series/swshp/SWSH139/", Localization.GERMANY, "Pikachu V-UNION"),
                Arguments.of("https://www.pokemon.com/de/pokemon-sammelkartenspiel/pokemon-karten/ss-series/swshp/SWSH140/", Localization.GERMANY, "Pikachu V-UNION"),
                Arguments.of("https://www.pokemon.com/de/pokemon-sammelkartenspiel/pokemon-karten/ss-series/swshp/SWSH141/", Localization.GERMANY, "Pikachu V-UNION"),
                Arguments.of("https://www.pokemon.com/de/pokemon-sammelkartenspiel/pokemon-karten/ss-series/swshp/SWSH142/", Localization.GERMANY, "Pikachu V-UNION"),
                Arguments.of("https://www.pokemon.com/es/jcc-pokemon/cartas-pokemon/ss-series/swshp/SWSH139/", Localization.SPAIN, "Pikachu V-UNIÓN"),
                Arguments.of("https://www.pokemon.com/es/jcc-pokemon/cartas-pokemon/ss-series/swshp/SWSH140/", Localization.SPAIN, "Pikachu V-UNIÓN"),
                Arguments.of("https://www.pokemon.com/es/jcc-pokemon/cartas-pokemon/ss-series/swshp/SWSH141/", Localization.SPAIN, "Pikachu V-UNIÓN"),
                Arguments.of("https://www.pokemon.com/es/jcc-pokemon/cartas-pokemon/ss-series/swshp/SWSH142/", Localization.SPAIN, "Pikachu V-UNIÓN")
        );
    }

}
