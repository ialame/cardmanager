package com.pcagrade.retriever.file;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@RetrieverTestConfiguration
public class SharedFileServiceTestConfig {

    @Bean
    public SharedFileService sharedFileService(@Value("${retriever.common-resource.path}") String commonResourcePath) {
        return new SharedFileService(commonResourcePath);
    }
}
