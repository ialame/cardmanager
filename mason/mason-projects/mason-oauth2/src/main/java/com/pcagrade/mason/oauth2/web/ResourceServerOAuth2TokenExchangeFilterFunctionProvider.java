package com.pcagrade.mason.oauth2.web;

import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServletBearerExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

public class ResourceServerOAuth2TokenExchangeFilterFunctionProvider implements IOAuth2TokenExchangeFilterFunctionProvider {

    @Override
    public ExchangeFilterFunction provide(String registrationId) {
        return new ServletBearerExchangeFilterFunction();
    }
}
