package com.pcagrade.mason.json.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.util.unit.DataSize;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

@Deprecated
public class JsonWebHelper { // TODO change to bean

    private JsonWebHelper() {}

    @Nonnull
    public static ExchangeStrategies createExchangeStrategies(@Nullable ObjectMapper objectMapper) {
        if (objectMapper == null) {
            return ExchangeStrategies.withDefaults();
        }
        return ExchangeStrategies.builder()
                .codecs(clientDefaultCodecsConfigurer -> {
                    clientDefaultCodecsConfigurer.defaultCodecs().maxInMemorySize((int) DataSize.parse("16MB").toBytes()); // TODO use property
                    clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                }).build();
    }
}
