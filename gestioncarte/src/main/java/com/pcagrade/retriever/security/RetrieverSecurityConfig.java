package com.pcagrade.retriever.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;

@Configuration
public class RetrieverSecurityConfig {

	private static final Logger LOGGER = LogManager.getLogger(RetrieverSecurityConfig.class);
	private static final String ADMIN_ROLE = "ADMIN"; // TODO move to authentik roles
	private static final String METRICS_ROLE = "METRICS"; // TODO move to authentik roles

	@Value("${retriever.security.login.enabled:true}")
	private boolean loginEnabled;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, Environment environment) throws Exception {
		if (!loginEnabled) {
			if (Arrays.asList(environment.getActiveProfiles()).contains("production")) {
				throw new IllegalStateException("Production profile requires authentication to be setup");
			}
			LOGGER.warn("Professional Card Retriever is running in insecure mode. Please configure a user and password to enable authentication.");
			return http.authorizeHttpRequests(r -> r.requestMatchers("/**").permitAll())
					.csrf(AbstractHttpConfigurer::disable)
					.build();
		}

		return http.authorizeHttpRequests(r -> r.requestMatchers("/**").authenticated()
						.requestMatchers("/actuator/shutdown").hasRole(ADMIN_ROLE)
						.requestMatchers("/actuator/restart").hasRole(ADMIN_ROLE)
						.requestMatchers("/actuator/**").hasRole(METRICS_ROLE)
						.requestMatchers("/login*", "/oauth/**").permitAll()
						.anyRequest().authenticated())
				.oauth2Login(o -> o.loginPage("/oauth2/authorization/authentik"))
				.oauth2Client(Customizer.withDefaults())
				.logout(Customizer.withDefaults())
				.exceptionHandling(h -> h.defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), new AntPathRequestMatcher("/api/**")))
				.build();
    }
}
