package com.pcagrade.retriever.extraction.consolidation.consolidator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class ConsolidatorConfiguration {

    @Bean
    @Order(0)
    public StringLiteralConsolidator stringLiteralConsolidator() {
        return new StringLiteralConsolidator();
    }

    @Bean
    @Order(1)
    public LiteralConsolidator literalConsolidator() {
        return new LiteralConsolidator();
    }

    @Bean
    @Order(2)
    public MapConsolidator mapConsolidator() {
        return new MapConsolidator();
    }

    @Bean
    @Order(3)
    public ListConsolidator listConsolidator() {
        return new ListConsolidator();
    }

    @Bean
    @Order(4)
    public SetConsolidator setConsolidator() {
        return new SetConsolidator();
    }
    @Bean
    @Order(5)
    public RecordConsolidator recordConsolidator() {
        return new RecordConsolidator();
    }

    @Bean
    @Order(Integer.MAX_VALUE)
    public ObjectConsolidator objectConsolidator() {
        return new ObjectConsolidator();
    }
}
