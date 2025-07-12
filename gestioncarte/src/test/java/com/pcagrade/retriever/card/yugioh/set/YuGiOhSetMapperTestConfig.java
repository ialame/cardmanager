package com.pcagrade.retriever.card.yugioh.set;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.yugioh.set.translation.YuGiOhSetTranslationMapper;
import com.pcagrade.retriever.card.yugioh.set.translation.YuGiOhSetTranslationMapperImpl;
import org.springframework.context.annotation.Bean;

@RetrieverTestConfiguration
public class YuGiOhSetMapperTestConfig {

    @Bean
    public YuGiOhSetTranslationMapper yuGiOhSetTranslationMapper() {
        return new YuGiOhSetTranslationMapperImpl();
    }


    @Bean
    public YuGiOhSetMapper yuGiOhSetMapper() {
        return new YuGiOhSetMapperImpl();
    }
}
