package com.pcagrade.retriever.extraction.consolidation.source;

import jakarta.annotation.Nonnull;

public record ConsolidationSource<T>(
        T value,
        int weight
) implements IConsolidationSource<T> {

    @Nonnull
    @Override
    public <U> IConsolidationSource<U> with(U newValue) {
        return new ConsolidationSource<>(newValue, weight);
    }

    @Nonnull
    @Override
    public T getValue() {
        return value;
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
