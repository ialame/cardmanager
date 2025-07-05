package com.pcagrade.retriever.card.yugioh;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.extraction.status.CardExtractionStatusTestConfig;
import com.pcagrade.retriever.card.yugioh.translation.YuGiOhCardTranslationMapper;
import com.pcagrade.retriever.card.yugioh.translation.YuGiOhCardTranslationMapperImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({CardExtractionStatusTestConfig.class})
public class YuGiOhCardMapperTestConfig {

    @Bean
    public YuGiOhCardTranslationMapper yuGiOhCardTranslationMapper() {
        return new YuGiOhCardTranslationMapperImpl();
    }


    @Bean
    public YuGiOhCardMapper yuGiOhCardMapper() {
        return new YuGiOhCardMapperImpl();
    }
}
