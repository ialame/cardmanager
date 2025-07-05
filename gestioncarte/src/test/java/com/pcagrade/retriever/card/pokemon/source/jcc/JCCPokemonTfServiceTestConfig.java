package com.pcagrade.retriever.card.pokemon.source.jcc;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.serie.PokemonSerieTestConfig;
import com.pcagrade.retriever.card.pokemon.translation.GenericNameParserTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({PokemonSerieTestConfig.class, GenericNameParserTestConfig.class})
public class JCCPokemonTfServiceTestConfig {

    @Bean
    public IJCCPokemonTfParser jccPokemonTfParser() {
        return new JCCPokemonTfParser();
    }

    @Bean
    public JCCPokemonTfService jccPokemonTfService() {
        return new JCCPokemonTfService();
    }

}
