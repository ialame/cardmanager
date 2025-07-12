package com.pcagrade.retriever.card.pokemon.source.bulbapedia.trainer;

import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

import java.util.List;

@RetrieverTestConfiguration
public class BulbapediaTrainerTestConfig {

    private int lastId = 0;

    private BulbapediaTrainer create(String name, String t) {
        var trainer = new BulbapediaTrainer();

        trainer.setId(++lastId);
        trainer.setName(name);
        trainer.setTrainer(t);
        return trainer;
    }

    @Bean
    public BulbapediaTrainerRepository bulbapediaTrainerRepository() {
        var list = List.of(
                create("Professor's Research", "Professor Willow"),
                create("Professor's Research", "Professor Magnolia"),
                create("Professor's Research", "Professor Oak"),
                create("Professor's Research", "Professor Rowan"),
                create("Professor's Research", "Professor Juniper"),
                create("Boss's Orders", "Giovanni"),
                create("Boss's Orders", "Cyrus"),
                create("Boss's Orders", "Lysandre")
        );

        var bulbapediaTrainerRepository = RetrieverTestUtils.mockRepository(BulbapediaTrainerRepository.class, list, BulbapediaTrainer::getId);

        Mockito.when(bulbapediaTrainerRepository.countByNameIgnoreCaseAndTrainerIgnoreCase(Mockito.anyString(), Mockito.anyString())).then(i -> {
            var arg0 = i.getArgument(0, String.class);
            var arg1 = i.getArgument(1, String.class);

            return (int) list.stream()
                    .filter(t -> t.getName().equalsIgnoreCase(arg0) && t.getTrainer().equalsIgnoreCase(arg1))
                    .count();
        });
        return bulbapediaTrainerRepository;
    }

    @Bean
    public BulbapediaTrainerService bulbapediaTrainerService() {
        return new BulbapediaTrainerService();
    }

}
