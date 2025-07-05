package com.pcagrade.retriever.parser.wiki.result.ask;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WikiAskConfiguration {

    @Bean
    public SimpleModule wikiAskModule() {
        var module = new SimpleModule("WikiAskModule");

        module.addDeserializer(WikiAskQuery.class, new WikiAskDeserializer());
        return module;
    }
}
