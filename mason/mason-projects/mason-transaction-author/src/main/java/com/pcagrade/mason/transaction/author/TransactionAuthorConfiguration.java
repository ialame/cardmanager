package com.pcagrade.mason.transaction.author;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@AutoConfigurationPackage
public class TransactionAuthorConfiguration {

    @Bean
    public AuthorNameResolver authorNameResolver(@Autowired(required = false) List<IAuthorNameProvider> providers) {
        return new AuthorNameResolver(providers);
    }
}
