package com.pcagrade.retriever.card.yugioh.set;

import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

@RetrieverTestConfiguration
public class YuGiOhSetRepositoryTestConfig {

    @Bean
    public YuGiOhSetRepository yuGiOhSetRepository() {
        var list = List.of(YuGiOhSetTestProvider.amazingDefenders());

        return RetrieverTestUtils.mockRepository(YuGiOhSetRepository.class, list, YuGiOhSet::getId);
    }

}
