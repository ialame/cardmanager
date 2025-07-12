package com.pcagrade.retriever.parser;

import jakarta.annotation.Nonnull;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.unit.DataSize;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

public class RetrieverHTTPHelper {

    private RetrieverHTTPHelper() {}

    @Deprecated
    @Nonnull
    public static ExchangeStrategies createExchangeStrategies(String maxInMemorySize) {
        return ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize((int) DataSize.parse(maxInMemorySize).toBytes()))
                .build();
    }

    @Nonnull
    public static ReactorClientHttpConnector createReactorClientHttpConnector(String name, int pool) {
        return new ReactorClientHttpConnector(HttpClient.create(ConnectionProvider.builder(name)
                        .maxConnections(pool)
                        .pendingAcquireMaxCount(1000)
                        .pendingAcquireTimeout(Duration.ofMinutes(2))
                        .metrics(true)
                        .build())
                .followRedirect(true)
                .compress(true));
    }

}
