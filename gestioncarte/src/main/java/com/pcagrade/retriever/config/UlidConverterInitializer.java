package com.pcagrade.retriever.config;

import com.github.f4b6a3.ulid.Ulid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.stereotype.Component;

@Component
public class UlidConverterInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(UlidConverterInitializer.class);

    private final Converter<String, Ulid> ulidConverter;
    private final FormattingConversionService conversionService;

    public UlidConverterInitializer(Converter<String, Ulid> ulidConverter, FormattingConversionService conversionService) {
        this.ulidConverter = ulidConverter;
        this.conversionService = conversionService;
        logger.info("UlidConverterInitializer created with converter: {}", ulidConverter);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info("Registering UlidConverter in ConversionService at context refresh: {}", ulidConverter);
        conversionService.addConverter(ulidConverter);
    }
}