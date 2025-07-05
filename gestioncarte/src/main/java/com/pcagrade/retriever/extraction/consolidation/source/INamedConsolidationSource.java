package com.pcagrade.retriever.extraction.consolidation.source;

import org.apache.commons.lang3.StringUtils;

public interface INamedConsolidationSource<T> extends IConsolidationSource<T> {

    String getName();

    @Override
    default boolean isSameSource(IConsolidationSource<?> other) {
        return other instanceof INamedConsolidationSource<?> named && StringUtils.equals(getName(), named.getName());
    }
}
