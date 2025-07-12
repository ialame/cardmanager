package com.pcagrade.mason.web.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcagrade.mason.oauth2.web.IOAuth2TokenExchangeFilterFunctionProvider;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.util.unit.DataSize;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class WebClientConfigurer implements IWebClientConfigurer, ApplicationContextAware {

    private static final Logger LOGGER = LogManager.getLogger(WebClientConfigurer.class);

    private ApplicationContext applicationContext;
    private final Map<Class<?>, Object> beans = new HashMap<>();

    @Override
    public IWebClientConfigurer json() {
        return new Configuration().json();
    }

    @Override
    public IWebClientConfigurer oauth2(String registrationId) {
        return new Configuration().oauth2(registrationId);
    }

    @Override
    public Consumer<WebClient.Builder> build() {
        return new Configuration().build();
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private class Configuration implements IWebClientConfigurer {

        private final List<Consumer<WebClient.Builder>> consumers = new ArrayList<>();

        @Override
        public IWebClientConfigurer json() {
            var objectMapper = getBean(ObjectMapper.class);

            consumers.add(b -> b.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .exchangeStrategies(ExchangeStrategies.builder()
                            .codecs(clientDefaultCodecsConfigurer -> {
                                clientDefaultCodecsConfigurer.defaultCodecs().maxInMemorySize((int) DataSize.parse("16MB").toBytes()); // TODO use property
                                if (objectMapper != null) {
                                    clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                                    clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                                } else {
                                    LOGGER.warn("ObjectMapper not found. Using default Codecs.");
                                }
                            }).build()));
            return this;
        }

        @Override
        public IWebClientConfigurer oauth2(String registrationId) {
            var provider = getBean(IOAuth2TokenExchangeFilterFunctionProvider.class);

            if (provider == null) {
                LOGGER.warn("OAuth2 configuration not found. Skipping OAuth2 configuration.");
                return this;
            }
            consumers.add(b -> b.filter(provider.provide(registrationId)));
            return this;
        }


        @Override
        public Consumer<WebClient.Builder> build() {
            return b -> consumers.forEach(c -> c.accept(b));
        }

        @SuppressWarnings("unchecked")
        @Nullable
        private <T> T getBean(Class<T> clazz) {
            synchronized (beans) {
                return (T) beans.computeIfAbsent(clazz, k -> {
                    try {
                        return applicationContext.getBean(clazz);
                    } catch (BeansException e) {
                        LOGGER.trace("Bean of type {} not found", clazz::getName);
                        return null;
                    }
                });
            }
        }
    }
}
