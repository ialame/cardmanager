package com.pcagrade.mason.oauth2;

import com.pcagrade.mason.oauth2.author.OAuth2AuthorConfiguration;
import com.pcagrade.mason.oauth2.web.ClientOAuth2TokenExchangeFilterFunctionProvider;
import com.pcagrade.mason.oauth2.web.IOAuth2TokenExchangeFilterFunctionProvider;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "mason.oauth2", name = "enabled", matchIfMissing = true)
@AutoConfigurationPackage
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@Import({ OAuth2AuthorConfiguration.class })
@ConditionalOnClass({ ClientRegistrationRepository.class, OAuth2AuthorizedClientRepository.class })
@AutoConfigureAfter(OAuth2ClientAutoConfiguration.class)
public class MasonOAuth2ClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    @ConditionalOnBean({ ClientRegistrationRepository.class, OAuth2AuthorizedClientRepository.class })
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2Login(o -> o.loginPage("/oauth2/authorization/authentik")) // TODO config
                .oauth2Client(Customizer.withDefaults())
                .logout(Customizer.withDefaults())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(IOAuth2TokenExchangeFilterFunctionProvider.class)
    @ConditionalOnBean({ ClientRegistrationRepository.class, OAuth2AuthorizedClientRepository.class })
    @ConditionalOnClass({ ExchangeFilterFunction.class, ServletOAuth2AuthorizedClientExchangeFilterFunction.class })
    public ClientOAuth2TokenExchangeFilterFunctionProvider oauth2TokenExchangeFilterFunctionProvider(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientRepository authorizedClientRepository) {
        return new ClientOAuth2TokenExchangeFilterFunctionProvider(clientRegistrationRepository, authorizedClientRepository);
    }
}
