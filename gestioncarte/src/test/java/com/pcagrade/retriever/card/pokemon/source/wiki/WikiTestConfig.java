package com.pcagrade.retriever.card.pokemon.source.wiki;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.feature.FeatureTestConfig;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestConfig;
import com.pcagrade.retriever.card.pokemon.source.wiki.url.WikiUrlTestConfig;
import com.pcagrade.retriever.card.pokemon.translation.GenericNameParserTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({ WikiUrlTestConfig.class, WikiTestConfig.WikiParserTestConfig.class, WikiParserConfiguration.class })
public class WikiTestConfig {

    @RetrieverTestConfiguration
    @Import({ GenericNameParserTestConfig.class, PokemonSetTestConfig.class, FeatureTestConfig.class })
    public static class WikiParserTestConfig {

        @Bean
        public PokepediaFrParser pokepediaFrParser() {
            return new PokepediaFrParser();
        }

        @Bean
        public PokeWikiDeParser pokeWikiDeParser() {
            return new PokeWikiDeParser();
        }

        @Bean
        public WikiDexNetParser wikiDexNetParser() {
            return new WikiDexNetParser();
        }

        @Bean
        public WikiPokemonCentralItParser wikiPokemonCentralItParser() {
            return new WikiPokemonCentralItParser();
        }
    }

    @Bean
    public WikiFeaturePatternService wikiFeaturePatternService() {
        return new WikiFeaturePatternService();
    }

}
