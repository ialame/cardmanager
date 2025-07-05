package com.pcagrade.retriever.card.pokemon.trainer;

import com.pcagrade.mason.localization.Localization;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KnownTrainerService {

    @Autowired
    private KnownTrainerRepository knownTrainerRepository;


    public Optional<KnownTrainer> extractTrainer(Localization localization, String name) {
        var list =  knownTrainerRepository.findByLocalizationAndLikeNameIgnoreCase(localization, name);

        if (CollectionUtils.isEmpty(list)) {
            return Optional.empty();
        }
        return Optional.of(list.get(0));
    }
}
