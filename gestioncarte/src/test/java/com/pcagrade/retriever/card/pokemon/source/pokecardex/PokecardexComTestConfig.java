package com.pcagrade.retriever.card.pokemon.source.pokecardex;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.source.pokecardex.code.PokecardexComCodeTestConfig;
import com.pcagrade.retriever.card.pokemon.trainer.KnownTrainerTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({PokecardexComCodeTestConfig.class, KnownTrainerTestConfig.class})
public class PokecardexComTestConfig {

    @Bean
    public PokecardexComParser pokecardexComParser() {
        return new PokecardexComParser();
    }

    @Bean
    public PokecardexComService pokecardexComService() {
        return new PokecardexComService();
    }
}
