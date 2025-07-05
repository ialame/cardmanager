package com.pcagrade.retriever.card.onepiece.serie;

import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.onepiece.serie.translation.OnePieceSerieTranslationMapper;
import com.pcagrade.retriever.card.onepiece.serie.translation.OnePieceSerieTranslationMapperImpl;
import org.springframework.context.annotation.Bean;

@RetrieverTestConfiguration
public class OnePieceSerieTestConfig {

    @Bean
    public OnePieceSerieRepository onePieceSerieRepository() {
        return RetrieverTestUtils.mockRepository(OnePieceSerieRepository.class);
    }

    @Bean
    public OnePieceSerieTranslationMapper onePieceSerieTranslationMapper() {
        return new OnePieceSerieTranslationMapperImpl();
    }

    @Bean
    public OnePieceSerieMapper onePieceSerieMapper() {
        return new OnePieceSerieMapperImpl();
    }

    @Bean
    public OnePieceSerieService onePieceSerieService() {
        return new OnePieceSerieService();
    }
}
