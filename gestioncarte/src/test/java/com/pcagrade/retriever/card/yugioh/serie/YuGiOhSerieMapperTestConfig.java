package com.pcagrade.retriever.card.yugioh.serie;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.yugioh.serie.transaltion.YuGiOhSerieTranslationMapperTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({YuGiOhSerieTranslationMapperTestConfig.class})
public class YuGiOhSerieMapperTestConfig {

    @Bean
    public YuGiOhSerieMapper yuGiOhSerieMapper() {
        return new YuGiOhSerieMapperImpl();
    }
}
