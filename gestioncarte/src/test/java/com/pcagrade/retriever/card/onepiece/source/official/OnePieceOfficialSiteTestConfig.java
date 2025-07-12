package com.pcagrade.retriever.card.onepiece.source.official;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.onepiece.serie.OnePieceSerieTestConfig;
import com.pcagrade.retriever.card.onepiece.set.OnePieceSetTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({OnePieceSerieTestConfig.class, OnePieceSetTestConfig.class})
public class OnePieceOfficialSiteTestConfig {

    @Bean
    public OnePieceOfficialSiteParser onePieceOfficialSiteParser() {
        return new OnePieceOfficialSiteParser();
    }

    @Bean
    public OnePieceOfficialSiteMapper onePieceOfficialSiteMapper() {
        return new OnePieceOfficialSiteMapperImpl();
    }

    @Bean
    public OnePieceOfficialSiteService onePieceOfficialSiteService() {
        return new OnePieceOfficialSiteService();
    }
}
