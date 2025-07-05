package com.pcagrade.painter;

import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PainterMvcConfiguration implements WebMvcConfigurer {

    @Value("${painter.image.storage-path}")
    private String imageStoragePath;

    @Value("${painter.image.cache-period:2592000}") // 30 days
    private int imageCachePeriod;

    @Override
    public void addResourceHandlers(@Nonnull ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations(new FileSystemResource(imageStoragePath))
                .setCachePeriod(imageCachePeriod);
    }
}
