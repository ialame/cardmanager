package com.pcagrade.retriever.card.yugioh.set;

import com.pcagrade.mason.jpa.revision.message.RevisionMessageConfiguration;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.yugioh.serie.YuGiOhSerieServiceTestConfig;
import com.pcagrade.retriever.card.yugioh.source.official.pid.OfficialSitePidTestConfig;
import com.pcagrade.retriever.card.yugioh.source.yugipedia.set.YugipediaSetTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({RevisionMessageConfiguration.class, YuGiOhSetMapperTestConfig.class, YuGiOhSetRepositoryTestConfig.class, YuGiOhSerieServiceTestConfig.class, OfficialSitePidTestConfig.class, YugipediaSetTestConfig.class})
public class YuGiOhSetTestConfig {

    @Bean
    public YuGiOhSetService yuGiOhSetService() {
        return new YuGiOhSetService();
    }
}
