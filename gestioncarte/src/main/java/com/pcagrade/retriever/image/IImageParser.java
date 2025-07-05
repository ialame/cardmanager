package com.pcagrade.retriever.image;

import reactor.core.publisher.Mono;

import java.util.Base64;

public interface IImageParser {
    Mono<byte[]> getImage(String url);

    default Mono<String> getImageBase64(String url) {
        return getImage(url).map(Base64.getEncoder()::encodeToString);
    }
}
