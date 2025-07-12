package com.pcagrade.retriever.card.pokemon.trainer;

import com.github.f4b6a3.ulid.UlidCreator;
import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.mason.localization.Localization;
import org.springframework.context.annotation.Bean;

import java.util.List;

@RetrieverTestConfiguration
public class KnownTrainerTestConfig {


    private KnownTrainer create(Localization localization, String name, String trainer) {
        return new KnownTrainer(UlidCreator.getUlid(), localization, name, name, trainer);
    }

    @Bean
    public KnownTrainerRepository knownTrainerRepository() {
        var list = List.of(
                create(Localization.USA, "Professor's Research", "Professor Willow"),
                create(Localization.USA, "Professor's Research", "Professor Magnolia"),
                create(Localization.USA, "Professor's Research", "Professor Oak"),
                create(Localization.USA, "Professor's Research", "Professor Rowan"),
                create(Localization.USA, "Professor's Research", "Professor Juniper"),
                create(Localization.USA, "Boss's Orders", "Giovanni"),
                create(Localization.USA, "Boss's Orders", "Cyrus"),
                create(Localization.USA, "Boss's Orders", "Lysandre")
        );

        return RetrieverTestUtils.mockRepository(KnownTrainerRepository.class, list, KnownTrainer::getId);
    }

    @Bean
    public KnownTrainerService knownTrainerService() {
        return new KnownTrainerService();
    }

}
