package com.pcagrade.retriever.card.artist;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.CardTestConfig;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({ CardTestConfig.class })
public class CardArtistTestConfig {

    @Bean
    public CardArtistRepository cardArtistRepository() {
        return Mockito.mock(CardArtistRepository.class);
    }

    @Bean
    public CardArtistService cardArtistService() {
        return new CardArtistService();
    }


}
