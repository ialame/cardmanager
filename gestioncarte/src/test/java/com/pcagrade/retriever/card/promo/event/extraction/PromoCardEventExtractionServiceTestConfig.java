package com.pcagrade.retriever.card.promo.event.extraction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.PokemonCardServiceTestConfig;
import com.pcagrade.retriever.card.promo.PromoCardTestConfig;
import com.pcagrade.retriever.card.promo.event.PromoCardEventTestConfig;
import com.pcagrade.retriever.card.promo.event.extraction.handler.PromoEventHandlerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({ PokemonCardServiceTestConfig.class, PromoCardTestConfig.class, PromoCardEventTestConfig.class, PromoEventHandlerConfiguration.class })
public class PromoCardEventExtractionServiceTestConfig {

    @Bean
    public PromoCardEventExtractionService promoCardEventExtractionService(ObjectMapper objectMapper) {
        return new PromoCardEventExtractionService(objectMapper);
    }

}
