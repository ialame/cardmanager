package com.pcagrade.retriever.card.pokemon;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.field.mapper.FieldMappingService;
import com.pcagrade.retriever.field.mapper.FieldMappingTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({ FieldMappingTestConfig.class })
public class PokemonFieldMappingServiceTestConfig {

    @Bean
    public PokemonFieldMappingService pokemonFieldMappingService(FieldMappingService fieldMappingService) {
        return new PokemonFieldMappingService(fieldMappingService);
    }

}
