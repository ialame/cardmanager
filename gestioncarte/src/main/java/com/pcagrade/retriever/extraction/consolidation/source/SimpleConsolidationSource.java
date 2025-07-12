package com.pcagrade.retriever.extraction.consolidation.source;

import jakarta.annotation.Nonnull;

public record SimpleConsolidationSource<T>(
        T value,
        int weight,
        String name,
        String link
) implements IConsolidationSource<T>, INamedConsolidationSource<T>, ILinkedConsolidationSource<T> {
    @Nonnull
    @Override
    public <U> IConsolidationSource<U> with(U newValue) {
        return new SimpleConsolidationSource<>(newValue, weight, name, link);
    }

    public static <T> SimpleConsolidationSource<T> from(IConsolidationSource<T> source) {
        var name = source instanceof INamedConsolidationSource<?> named ? named.getName() : "";
        var link = source instanceof ILinkedConsolidationSource<?> linked ? linked.getLink() : "";

        return new SimpleConsolidationSource<>(source.getValue(), source.getWeight(), name, link);
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

    @Override
    public String getLink() {
        return link;
    }

    @Override
    public String getName() {
        return name;
    }
}
