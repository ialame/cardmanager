package com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction.handler;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.PokemonCardMapperTestConfig;
import com.pcagrade.retriever.card.pokemon.PokemonCardServiceTestConfig;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.expansion.ExpansionBulbapediaTestConfig;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.parser.BulbapediaParserTestConfig;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.trainer.BulbapediaTrainerTestConfig;
import com.pcagrade.retriever.card.tag.CardTagTestConfig;
import org.junit.jupiter.api.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({ PokemonCardServiceTestConfig.class, PokemonCardMapperTestConfig.class, ExpansionBulbapediaTestConfig.class, BulbapediaParserTestConfig.class, BulbapediaTrainerTestConfig.class, CardTagTestConfig.class })
public class BulbapediaMappingHandlerTestConfig {

    @Bean
    @Order(1)
    public IBulbapediaMappingHandler bulbapediaMappingFeatureHandler() {
        return new BulbapediaMappingFeatureHandler();
    }

    @Bean
    @Order(2)
    public IBulbapediaMappingHandler bulbapediaMappingBracketHandler() {
        return new BulbapediaMappingBracketHandler();
    }

    @Bean
    @Order(3)
    public IBulbapediaMappingHandler bulbapediaMappingFormeHandler() {
        return new BulbapediaMappingFormeHandler();
    }

    @Bean
    @Order(4)
    public IBulbapediaMappingHandler bulbapediaMappingRegionalFormHandler() {
        return new BulbapediaMappingRegionalFormHandler();
    }

    @Bean
    @Order(5)
    public IBulbapediaMappingHandler bulbapediaMappingTeraTypeHandler() {
        return new BulbapediaMappingTeraTypeHandler();
    }
}
