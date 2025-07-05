package com.pcagrade.mason.oauth2.author;

import com.pcagrade.mason.transaction.author.IAuthorNameProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(IAuthorNameProvider.class)
public class OAuth2AuthorConfiguration {

    @Bean
    public OAuth2AuthorNameProvider oauth2AuthorNameProvider() {
        return new OAuth2AuthorNameProvider();
    }
}
