package com.pcagrade.retriever;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcagrade.mason.localization.LocalizationAutoConfiguration;
import com.pcagrade.mason.ulid.UlidAutoConfiguration;
import com.pcagrade.mason.web.client.WebClientAutoConfiguration;
import com.pcagrade.painer.client.EnablePainter;
import com.pcagrade.retriever.cache.CacheConfiguration;
import com.pcagrade.retriever.cache.CacheService;
import com.pcagrade.retriever.card.yugioh.source.ygoprodeck.YgoProDeckJacksonModule;
import com.pcagrade.retriever.date.DateMapper;
import com.pcagrade.retriever.localization.LocalizationMapper;
import com.pcagrade.retriever.localization.LocalizationMapperImpl;
import com.pcagrade.retriever.parser.HTMLParser;
import com.pcagrade.retriever.parser.IHTMLParser;
import com.pcagrade.retriever.transaction.TransactionService;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@EnablePainter
@EnableCaching
@TestConfiguration
@Import({ WebClientAutoConfiguration.class, UlidAutoConfiguration.class, LocalizationAutoConfiguration.class, RetrieverConfig.class, CacheConfiguration.class })
public class RetrieverTestConfig {

    @Bean
    public CacheService cacheService() {
        return new CacheService();
    }

    @Bean
    public IHTMLParser htmlParser() {
        return new HTMLParser("16MB");
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return Mockito.mock(PlatformTransactionManager.class);
    }

    @Bean
    public TransactionService transactionService() {
        return new TransactionService();
    }

    @Bean
    public YgoProDeckJacksonModule ygoProDeckJacksonModule() {
        return new YgoProDeckJacksonModule();
    }

    @Bean
    public ObjectMapper objectMapper(List<Module> modules) {
        var mapper = new ObjectMapper();

        mapper.registerModules(modules);
        mapper.findAndRegisterModules();
        return mapper;
    }

    @Bean
    public LocalizationMapper localizationMapper() {
        return new LocalizationMapperImpl();
    }

    @Bean
    @ConditionalOnMissingBean(DateMapper.class)
    public DateMapper dateMapper() {
        return new DateMapper();
    }
}
