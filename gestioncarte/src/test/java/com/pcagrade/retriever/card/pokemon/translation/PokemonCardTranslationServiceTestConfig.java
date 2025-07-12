package com.pcagrade.retriever.card.pokemon.translation;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.PokemonFieldMappingServiceTestConfig;
import com.pcagrade.retriever.card.pokemon.translation.translator.PokemonCardTranslatorServiceTestConfig;
import com.pcagrade.retriever.extraction.consolidation.ConsolidationServiceTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({ IPokemonCardTranslatorFactoryTestConfig.class, PokemonCardTranslatorServiceTestConfig.class, ConsolidationServiceTestConfig.class, PokemonFieldMappingServiceTestConfig.class })
public class PokemonCardTranslationServiceTestConfig {

    @Bean
    public PokemonCardTranslationService pokemonCardTranslationService() {
        return new PokemonCardTranslationService();
    }

}
