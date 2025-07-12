package com.pcagrade.retriever.card.pokemon.translation.translator;

import com.pcagrade.retriever.annotation.RetrieverTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(PokemonCardTranslatorServiceTestConfig.class)
class PokemonCardTranslatorServiceShould {

    @Autowired
    PokemonCardTranslatorService pokemonCardTranslatorService;

    @Test
    void getTranslators() {
        var translators = pokemonCardTranslatorService.getTranslators();

        assertThat(translators).isNotEmpty();
    }

    @Test
    void getWeight() {
        var weight = pokemonCardTranslatorService.getWeight("ptcgo-data");

        assertThat(weight).isEqualTo(1);
    }

}
