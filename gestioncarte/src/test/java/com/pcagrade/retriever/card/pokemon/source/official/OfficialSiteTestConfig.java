package com.pcagrade.retriever.card.pokemon.source.official;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.source.official.path.OfficialSiteSetPathTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import(OfficialSiteSetPathTestConfig.class)
public class OfficialSiteTestConfig {

    @Bean
    public OfficialSiteParser officialSiteParser() {
        return new OfficialSiteParser();
    }

    @Bean
    public OfficialSiteTranslationService officialSiteTranslationService() {
        return new OfficialSiteTranslationService();
    }

}
