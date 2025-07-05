package com.pcagrade.mason.web.client;


import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigurationPackage
public class WebClientAutoConfiguration {

    @Bean
    public WebClientConfigurer webClientConfigurer() {
        return new WebClientConfigurer();
    }
}
