package com.pcagrade.retriever.card.yugioh.source.yugipedia.set;

import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetRepositoryTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import(YuGiOhSetRepositoryTestConfig.class)
public class YugipediaSetTestConfig {

    @Bean
    public YugipediaSetRepository yugipediaSetRepository() {
        return RetrieverTestUtils.mockRepository(YugipediaSetRepository.class);
    }

    @Bean
    public YugipediaSetMapper yugipediaSetMapper() {
        return new YugipediaSetMapperImpl();
    }

    @Bean
    public YugipediaSetService yugipediaSetService() {
        return new YugipediaSetService();
    }
}
