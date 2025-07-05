package com.pcagrade.retriever.extraction.consolidation;

import com.pcagrade.retriever.extraction.consolidation.consolidator.ConsolidatorConfiguration;
import com.pcagrade.retriever.extraction.consolidation.consolidator.IConsolidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

@Configuration
@Import(ConsolidatorConfiguration.class)
public class ConsolidationConfiguration {

    @Bean
    public ConsolidationService consolidationService(List<IConsolidator> consolidators) {
        return new ConsolidationService(consolidators);
    }
}
