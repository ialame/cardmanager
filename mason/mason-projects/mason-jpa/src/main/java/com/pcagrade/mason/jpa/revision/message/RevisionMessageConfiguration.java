package com.pcagrade.mason.jpa.revision.message;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RevisionMessageConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RevisionMessageService revisionMessageService() {
        return new RevisionMessageService();
    }

    @Bean
    public RevisionMessageAspect revisionMessageAspect(RevisionMessageService revisionMessageService) {
        return new RevisionMessageAspect(revisionMessageService);
    }

}
