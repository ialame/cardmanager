package com.pcagrade.retriever.card.pokemon.source.wiki;

import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(WikiTestConfig.class)
class WikiFeaturePatternServiceShould {

    @Autowired
    WikiFeaturePatternService wikiFeaturePatternService;

    @Test
    void getNames() {
        var names = wikiFeaturePatternService.getNames();

        assertThat(names).isNotEmpty();
    }

    @Test
    void searchPatterns() {
        var patterns = wikiFeaturePatternService.searchPatterns("wiki-fr-pokepedia", PokemonSetTestProvider.XY_ID);

        assertThat(patterns).isNotEmpty();
    }
}
