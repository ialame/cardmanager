package com.pcagrade.mason.web.client;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Consumer;


public interface IWebClientConfigurer {

    IWebClientConfigurer json();

    IWebClientConfigurer oauth2(String registrationId);

    Consumer<WebClient.Builder> build();
}
