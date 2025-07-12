package com.pcagrade.retriever.card.pokemon.translation;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.feature.FeatureTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({ FeatureTestConfig.class })
public class GenericNameParserTestConfig {

    @Bean
    public GenericNameParser genericNameParser() {
        return new GenericNameParser();
    }


}
