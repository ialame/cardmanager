package com.pcagrade.retriever.card.pokemon.source.bulbapedia.trainer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BulbapediaTrainerService {

    @Autowired
    private BulbapediaTrainerRepository bulbapediaTrainerRepository;

    public boolean isTrainer(String name, String trainer) {
        if (StringUtils.isAnyBlank(name, trainer)) {
            return false;
        }
        return bulbapediaTrainerRepository.countByNameIgnoreCaseAndTrainerIgnoreCase(name, trainer) > 0;
    }
}
