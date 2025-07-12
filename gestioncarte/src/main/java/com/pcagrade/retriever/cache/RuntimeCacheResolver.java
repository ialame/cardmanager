package com.pcagrade.retriever.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Collection;
import java.util.List;

public class RuntimeCacheResolver extends SimpleCacheResolver {

    public RuntimeCacheResolver(CacheManager cacheManager) {
        super(cacheManager);
    }

    @Override
    protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
        var type = context.getTarget().getClass();
        var cacheConfig = AnnotationUtils.findAnnotation(type, CacheConfig.class);

        if (cacheConfig != null) {
            var names = cacheConfig.cacheNames();

            if (names.length == 0) {
                return List.of(type.getSimpleName());
            }
            return List.of(names);
        }

        return super.getCacheNames(context);
    }
}
