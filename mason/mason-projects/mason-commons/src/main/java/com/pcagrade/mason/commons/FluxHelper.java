package com.pcagrade.mason.commons;

import jakarta.annotation.Nonnull;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;

public class FluxHelper {

    private FluxHelper() { }

    @Nonnull
    public static <T> List<T> blockList(@Nonnull Flux<T> flux) {
        return flux.collectList()
                .blockOptional()
                .orElseGet(Collections::emptyList);
    }
}
