package com.pcagrade.retriever.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfiguration {

    @Bean
    public CacheManager cacheManager() { // FIXME use jcache instead of spring cache
        return new ConcurrentMapCacheManager();
    }

    @Bean
    public CacheResolver runtimeCacheResolver(CacheManager cacheManager) {
        return new RuntimeCacheResolver(cacheManager);
    }

}
