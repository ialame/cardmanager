package com.pcagrade.retriever.card.yugioh;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.field.mapper.FieldMappingService;
import com.pcagrade.retriever.field.mapper.FieldMappingTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({ FieldMappingTestConfig.class })
public class YuGiOhFieldMappingServiceTestConfig {

    @Bean
    public YuGiOhFieldMappingService yuGiOhFieldMappingService(FieldMappingService fieldMappingService) {
        return new YuGiOhFieldMappingService(fieldMappingService);
    }

}
