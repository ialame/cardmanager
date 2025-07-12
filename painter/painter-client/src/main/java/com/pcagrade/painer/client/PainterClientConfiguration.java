package com.pcagrade.painer.client;

import com.pcagrade.mason.web.client.IWebClientConfigurer;
import com.pcagrade.painter.common.image.IImageService;
import com.pcagrade.painter.common.image.card.ICardImageService;
import com.pcagrade.painter.common.image.legacy.ILegacyImageService;
import com.pcagrade.painter.common.image.set.ISetImageService;
import com.pcagrade.painter.common.publicdata.PublicUrlService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PainterClientProperties.class)
public class PainterClientConfiguration {

    @Bean
    public IImageService imageService(PainterClientProperties painterClientProperties, IWebClientConfigurer webClientConfigurer) {
        return new ClientImageService(painterClientProperties, webClientConfigurer);
    }

    @Bean
    public ILegacyImageService legacyImageService(PainterClientProperties painterClientProperties, IWebClientConfigurer webClientConfigurer) {
        return new ClientLegacyImageService(painterClientProperties, webClientConfigurer);
    }

    @Bean
    public ICardImageService cardImageService(PainterClientProperties painterClientProperties, IWebClientConfigurer webClientConfigurer) {
        return new ClientCardImageService(painterClientProperties, webClientConfigurer);
    }

    @Bean
    public ISetImageService setImageService(PainterClientProperties painterClientProperties, IWebClientConfigurer webClientConfigurer) {
        return new ClientSetImageService(painterClientProperties, webClientConfigurer);
    }

    @Bean
    public PublicUrlService publicUrlService(PainterClientProperties painterClientProperties) {
        return new PublicUrlService(painterClientProperties.publicUrl());
    }
}
