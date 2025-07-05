package com.pcagrade.mason.oauth2;

import com.pcagrade.mason.oauth2.author.OAuth2AuthorConfiguration;
import com.pcagrade.mason.oauth2.web.IOAuth2TokenExchangeFilterFunctionProvider;
import com.pcagrade.mason.oauth2.web.ResourceServerOAuth2TokenExchangeFilterFunctionProvider;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServletBearerExchangeFilterFunction;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "mason.oauth2", name = "enabled", matchIfMissing = true)
@AutoConfigurationPackage
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@Import({ OAuth2AuthorConfiguration.class })
@ConditionalOnClass({ JwtDecoder.class, JwtAuthenticationProvider.class })
@AutoConfigureBefore(OAuth2ResourceServerAutoConfiguration.class)
public class MasonOAuth2ResourceServerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> { // FIXME add annotation for scope authorities
                    var jwtAuthenticationConverter = new JwtAuthenticationConverter();
                    var jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

                    jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
                    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter);
                }))
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(IOAuth2TokenExchangeFilterFunctionProvider.class)
    @ConditionalOnClass({ ExchangeFilterFunction.class, ServletBearerExchangeFilterFunction.class })
    public ResourceServerOAuth2TokenExchangeFilterFunctionProvider oauth2TokenExchangeFilterFunctionProvider() {
        return new ResourceServerOAuth2TokenExchangeFilterFunctionProvider();
    }
}
