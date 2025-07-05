package com.pcagrade.retriever.card.extraction.status;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import org.springframework.context.annotation.Bean;

@RetrieverTestConfiguration
public class CardExtractionStatusTestConfig {

    @Bean
    public CardExtractionStatusMapper cardExtractionStatusMapper() {
        return new CardExtractionStatusMapperImpl();
    }

}
