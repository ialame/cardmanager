package com.pcagrade.retriever.card;

import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.PokemonCardRepositoryTestConfig;
import org.springframework.context.annotation.Bean;

import java.util.List;

@RetrieverTestConfiguration
public class CardTestConfig {

    @Bean
    public CardRepository cardRepository() {
        List<Card> list = PokemonCardRepositoryTestConfig.CARDS.stream()
                .map(c -> (Card) c)
                .toList();

        return RetrieverTestUtils.mockRepository(CardRepository.class, list, Card::getId);
    }

}
