package com.pcagrade.mason.localization;

import com.pcagrade.mason.localization.jpa.LocalizationAttributeConverter;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigurationPackage
public class LocalizationAutoConfiguration {

    @Bean
    public LocalizationConverter localizationConverter() {
        return new LocalizationConverter();
    }

    @Bean
    public LocalizationGroupConverter localizationGroupConverter() {
        return new LocalizationGroupConverter();
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Converter.class, AttributeConverter.class})
    public static class Jpa {

        @Bean
        public LocalizationAttributeConverter localizationAttributeConverter() {
            return new LocalizationAttributeConverter();
        }
    }
}
