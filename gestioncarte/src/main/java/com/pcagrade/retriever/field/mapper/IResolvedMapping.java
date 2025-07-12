package com.pcagrade.retriever.field.mapper;

import java.util.function.Function;

@FunctionalInterface
public interface IResolvedMapping extends Function<String, String> {

    default String map(String source) {
        return apply(source);
    }
}
