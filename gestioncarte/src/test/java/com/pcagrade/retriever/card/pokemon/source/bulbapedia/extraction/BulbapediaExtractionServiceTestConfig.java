package com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.PokemonCardMapperTestConfig;
import com.pcagrade.retriever.card.pokemon.PokemonCardServiceTestConfig;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.expansion.ExpansionBulbapediaTestConfig;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction.handler.BulbapediaMappingHandlerTestConfig;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.parser.BulbapediaParserTestConfig;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.trainer.BulbapediaTrainerTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({ PokemonCardServiceTestConfig.class, PokemonCardMapperTestConfig.class, ExpansionBulbapediaTestConfig.class, BulbapediaParserTestConfig.class, BulbapediaTrainerTestConfig.class, BulbapediaMappingHandlerTestConfig.class })
public class BulbapediaExtractionServiceTestConfig {

    @Bean
    public BulbapediaExtractionService bulbapediaExtractionService() {
        return new BulbapediaExtractionService();
    }

}
