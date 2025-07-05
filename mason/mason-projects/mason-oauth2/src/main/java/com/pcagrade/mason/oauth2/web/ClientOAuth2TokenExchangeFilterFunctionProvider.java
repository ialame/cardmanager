package com.pcagrade.mason.oauth2.web;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

public class ClientOAuth2TokenExchangeFilterFunctionProvider implements IOAuth2TokenExchangeFilterFunctionProvider {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AuthorizedClientRepository authorizedClientRepository;

    public ClientOAuth2TokenExchangeFilterFunctionProvider(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientRepository authorizedClientRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authorizedClientRepository = authorizedClientRepository;
    }

    @Override
    public ExchangeFilterFunction provide(String registrationId) {
        var exchangeFilterFunction = new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository, authorizedClientRepository);

        exchangeFilterFunction.setDefaultClientRegistrationId(registrationId);
        return exchangeFilterFunction;
    }
}
