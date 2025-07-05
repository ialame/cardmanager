package com.pcagrade.retriever.card.promo;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.PokemonCardRepositoryTestConfig;
import com.pcagrade.retriever.card.promo.event.PromoCardEventTestConfig;
import com.pcagrade.retriever.card.promo.version.PromoCardVersionTestConfig;
import com.pcagrade.mason.ulid.UlidHelper;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;

@RetrieverTestConfiguration
@Import({ PokemonCardRepositoryTestConfig.class, PromoCardEventTestConfig.class, PromoCardVersionTestConfig.class })
public class PromoCardTestConfig {

    public static final List<PromoCard> PROMOS = PokemonCardRepositoryTestConfig.CARDS.stream()
            .<PromoCard>mapMulti((c, downstream) -> c.getPromoCards().forEach(downstream))
            .toList();

    @Bean
    public PromoCardRepository promoCardRepository() {
        var repository = RetrieverTestUtils.mockRepository(PromoCardRepository.class, PROMOS, PromoCard::getId);

        Mockito.when(repository.findAllByCardId(Mockito.any(Ulid.class))).thenAnswer(invocation -> {
            var cardId = invocation.getArgument(0, Ulid.class);

            return PROMOS.stream()
                    .filter(p -> UlidHelper.equals(p.getCard().getId(), cardId))
                    .toList();
        });
        Mockito.when(repository.findAllByEventIdIsNull()).thenAnswer(invocation -> PROMOS.stream()
                .filter(p -> p.getEvent() == null)
                .toList());

        return repository;
    }

    @Bean
    public PromoCardMapper promoCardMapper() {
        return new PromoCardMapperImpl();
    }

    @Bean
    public PromoCardService promoCardService() {
        return new PromoCardService();
    }
}
