package com.pcagrade.retriever.card.pokemon.feature;

import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

import java.util.List;

@RetrieverTestConfiguration
public class FeatureRepositoryTestConfig {

    @Bean
    public FeatureRepository featureRepository() {
        var list = List.of(
                FeatureTestProvider.mega(),
                FeatureTestProvider.ex(),
                FeatureTestProvider.exNonTera()
        );

        var featureRepository = RetrieverTestUtils.mockRepository(FeatureRepository.class, list, Feature::getId);

//		Mockito.when(featureRepository.findFirstByBulbapediaIgnoreCaseOrderById(mega.getName())).thenReturn(Optional.of(mega));
//		Mockito.when(featureRepository.findByBulbapediaIsNotNullAndImgHrefBulbapediaIsNullOrderById()).thenReturn(List.of(mega));
        Mockito.when(featureRepository.save(Mockito.any(Feature.class))).then(i -> i.getArgument(0));
        return featureRepository;
    }

}
