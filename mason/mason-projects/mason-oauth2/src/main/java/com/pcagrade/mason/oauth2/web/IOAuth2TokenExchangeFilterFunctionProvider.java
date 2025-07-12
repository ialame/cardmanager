package com.pcagrade.mason.oauth2.web;

import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

@FunctionalInterface
public interface IOAuth2TokenExchangeFilterFunctionProvider {

    ExchangeFilterFunction provide(String registrationId);
}
