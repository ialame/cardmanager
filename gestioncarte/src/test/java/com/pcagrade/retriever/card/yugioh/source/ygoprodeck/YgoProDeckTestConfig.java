package com.pcagrade.retriever.card.yugioh.source.ygoprodeck;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.yugioh.YuGiOhFieldMappingServiceTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({YuGiOhFieldMappingServiceTestConfig.class})
public class YgoProDeckTestConfig {

    @Bean
    public YgoProDeckParser ygoProDeckParser(@Value("${ygoprodeck-com.url}") String ygoProDeckUrl, @Autowired ObjectMapper objectMapper) {
        return new YgoProDeckParser(ygoProDeckUrl, objectMapper);
    }

    @Bean
    public YgoProDeckMapper ygoProDeckMapper() {
        return new YgoProDeckMapperImpl();
    }

    @Bean
    public YgoProDeckService ygoProDeckService() {
        return new YgoProDeckService();
    }
}
