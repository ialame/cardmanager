package com.pcagrade.retriever.card.pokemon;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.extraction.status.CardExtractionStatusTestConfig;
import com.pcagrade.retriever.card.pokemon.bracket.BracketTestConfig;
import com.pcagrade.retriever.card.pokemon.feature.FeatureTestConfig;
import com.pcagrade.retriever.card.promo.PromoCardTestConfig;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.parser.BulbapediaParserTestConfig;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationMapper;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationMapperImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({ PokemonFieldMappingServiceTestConfig.class, FeatureTestConfig.class, BracketTestConfig.class, BulbapediaParserTestConfig.class, CardExtractionStatusTestConfig.class, PromoCardTestConfig.class})
public class PokemonCardMapperTestConfig {

    @Bean
    public PokemonCardMapper pokemonCardMapper() {
        return new PokemonCardMapperImpl();
    }

    @Bean
    public PokemonCardTranslationMapper pokemonCardTranslationMapper() {
        return new PokemonCardTranslationMapperImpl();
    }
}
