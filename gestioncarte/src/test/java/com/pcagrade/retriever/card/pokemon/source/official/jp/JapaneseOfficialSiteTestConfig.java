package com.pcagrade.retriever.card.pokemon.source.official.jp;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.source.official.jp.source.JapaneseOfficialSiteSourceTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import(JapaneseOfficialSiteSourceTestConfig.class)
public class JapaneseOfficialSiteTestConfig {

    @Bean
    public JapaneseOfficialSiteParser japaneseOfficialSiteParser() {
        return new JapaneseOfficialSiteParser("https://www.pokemon-card.com/", "16MB");
    }

    @Bean
    public JapaneseOfficialSiteService japaneseOfficialSiteService() {
        return new JapaneseOfficialSiteService();
    }

}
