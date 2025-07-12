package com.pcagrade.retriever.card.pokemon.source.wiki;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WikiParserConfiguration {

    @Bean
    public PokemonWikiTranslatorFactory pokepediaFrFactory(IPokemonWikiParser pokepediaFrParser) {
        return new PokemonWikiTranslatorFactory(pokepediaFrParser);
    }

    @Bean
    public PokemonWikiTranslatorFactory pokeWikiDeFactory(IPokemonWikiParser pokeWikiDeParser) {
        return new PokemonWikiTranslatorFactory(pokeWikiDeParser);
    }

    @Bean
    public PokemonWikiTranslatorFactory wikiDexNetFactory(IPokemonWikiParser wikiDexNetParser) {
        return new PokemonWikiTranslatorFactory(wikiDexNetParser);
    }

    @Bean
    public PokemonWikiTranslatorFactory wikiPokemonCentralItFactory(IPokemonWikiParser wikiPokemonCentralItParser) {
        return new PokemonWikiTranslatorFactory(wikiPokemonCentralItParser);
    }
}
