package com.pcagrade.retriever.card.onepiece.extraction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.onepiece.OnePieceCardTestConfig;
import com.pcagrade.retriever.card.onepiece.serie.OnePieceSerieTestConfig;
import com.pcagrade.retriever.card.onepiece.set.OnePieceSetTestConfig;
import com.pcagrade.retriever.card.onepiece.source.official.OnePieceOfficialSiteTestConfig;
import com.pcagrade.retriever.card.promo.PromoCardTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({OnePieceCardTestConfig.class, OnePieceSetTestConfig.class, OnePieceSerieTestConfig.class, OnePieceOfficialSiteTestConfig.class, PromoCardTestConfig.class})
public class OnePieceCardExtractionTestConfig {

    @Bean
    public OnePieceCardExtractionService onePieceCardExtractionService(ObjectMapper mapper) {
        return new OnePieceCardExtractionService(mapper);
    }
}
