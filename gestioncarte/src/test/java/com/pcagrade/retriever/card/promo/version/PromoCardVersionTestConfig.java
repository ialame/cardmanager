package com.pcagrade.retriever.card.promo.version;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.TradingCardGame;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

import java.util.List;

@RetrieverTestConfiguration
public class PromoCardVersionTestConfig {

    public static final Ulid STAFF_ID = UlidCreator.getUlid();

    private static PromoCardVersion staff() {
        var version = new PromoCardVersion();

        version.setId(STAFF_ID);
        version.setName("Staff");
        version.setTcg(TradingCardGame.POKEMON);
        return version;
    }

    @Bean
    public PromoCardVersionRepository promoCardVersionRepository() {
        var list = List.of(
                staff()
        );

        var repository = RetrieverTestUtils.mockRepository(PromoCardVersionRepository.class, list, PromoCardVersion::getId);

        Mockito.when(repository.findAllByTcg(Mockito.any(TradingCardGame.class))).then(i-> {
            var tcg = i.getArgument(0, TradingCardGame.class);

            return list.stream()
                    .filter(v -> v.getTcg() == tcg)
                    .toList();
        });
        return repository;
    }
}
