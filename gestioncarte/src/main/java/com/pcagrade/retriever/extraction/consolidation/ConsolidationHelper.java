package com.pcagrade.retriever.extraction.consolidation;

import org.springframework.core.ResolvableType;

import jakarta.annotation.Nonnull;

public class ConsolidationHelper {

    private ConsolidationHelper() {}

    @Nonnull
    public static Class<?> getRawClass(ResolvableType resolvedType) {
        var rawClass = resolvedType.getRawClass();

        if (rawClass == null) {
            throw new ConsolidationException("Type: " + resolvedType + " must be a class");
        }
        return rawClass;
    }
}
