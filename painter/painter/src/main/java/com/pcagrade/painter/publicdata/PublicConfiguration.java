package com.pcagrade.painter.publicdata;

import com.pcagrade.painter.common.publicdata.PublicUrlService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PublicConfiguration {

    @Bean
    public PublicUrlService publicUrlService(@Value("${painter.public-url:}") String publicUrl) {
        return new PublicUrlService(publicUrl);
    }
}
