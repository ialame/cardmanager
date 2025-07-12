package com.pcagrade.retriever.card.yugioh.serie.transaltion;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.yugioh.serie.translation.YuGiOhSerieTranslationMapper;
import com.pcagrade.retriever.card.yugioh.serie.translation.YuGiOhSerieTranslationMapperImpl;
import org.springframework.context.annotation.Bean;

@RetrieverTestConfiguration
public class YuGiOhSerieTranslationMapperTestConfig {

    @Bean
    public YuGiOhSerieTranslationMapper yuGiOhSerieTranslationMapper() {
        return new YuGiOhSerieTranslationMapperImpl();
    }
}
