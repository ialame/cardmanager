package com.pcagrade.retriever.card.yugioh.source.official;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.yugioh.YuGiOhCardMapperTestConfig;
import com.pcagrade.retriever.card.yugioh.YuGiOhFieldMappingServiceTestConfig;
import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetTestConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({YuGiOhSetTestConfig.class, YuGiOhCardMapperTestConfig.class, YuGiOhFieldMappingServiceTestConfig.class})
public class OfficialSiteTestConfig {

    @Bean
    public YuGiOhOfficialSiteParser officialSiteParser(@Value("${db-yugioh-com.url}") String dbYuGiOhComUrl) {
        return new YuGiOhOfficialSiteParser(dbYuGiOhComUrl);
    }

    @Bean
    public YuGiOhOfficialSiteMapper officialSiteMapper() {
        return new YuGiOhOfficialSiteMapperImpl();
    }

    @Bean
    public YuGiOhOfficialSiteService officialSiteService() {
        return new YuGiOhOfficialSiteService();
    }
}
