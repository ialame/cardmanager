package com.pcagrade.retriever.card.yugioh.serie;

import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.yugioh.set.translation.YuGiOhSerieTranslationRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({YuGiOhSerieMapperTestConfig.class})
public class YuGiOhSerieServiceTestConfig {

    @Bean
    public YuGiOhSerieRepository yuGiOhSerieRepository() {
        return RetrieverTestUtils.mockRepository(YuGiOhSerieRepository.class);
    }

    @Bean
    public YuGiOhSerieTranslationRepository yuGiOhSerieTranslationRepository() {
        return RetrieverTestUtils.mockRepository(YuGiOhSerieTranslationRepository.class);
    }

    @Bean
    public YuGiOhSerieService yuGiOhSerieService() {
        return new YuGiOhSerieService();
    }
}
