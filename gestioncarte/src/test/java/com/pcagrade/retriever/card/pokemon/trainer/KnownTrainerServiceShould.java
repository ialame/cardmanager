package com.pcagrade.retriever.card.pokemon.trainer;

import com.pcagrade.retriever.annotation.RetrieverTest;
import org.springframework.beans.factory.annotation.Autowired;

@RetrieverTest(KnownTrainerTestConfig.class)
class KnownTrainerServiceShould {

    @Autowired
    KnownTrainerService knownTrainerService;

}
