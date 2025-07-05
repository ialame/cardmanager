package com.pcagrade.retriever.extraction.consolidation.source;

import jakarta.annotation.Nonnull;
import java.util.function.Function;

public interface IConsolidationSource<T> {
    @Nonnull
    default <U> IConsolidationSource<U> map(Function<T, U> mapper) {
        return with(mapper.apply(getValue()));
    }

    @Nonnull
    <U> IConsolidationSource<U> with(U newValue);

    @Nonnull
    T getValue();

    int getWeight();

    default boolean isSameSource(IConsolidationSource<?> other) {
        return false;
    }
}
