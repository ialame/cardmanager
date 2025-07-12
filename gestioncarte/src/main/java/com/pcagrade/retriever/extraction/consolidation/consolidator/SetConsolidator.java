package com.pcagrade.retriever.extraction.consolidation.consolidator;

import com.pcagrade.retriever.extraction.consolidation.ConsolidationHelper;
import org.springframework.core.ResolvableType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SetConsolidator extends AbstractIterableConsolidator {

    @Override
    public boolean canConsolidate(ResolvableType type) {
        return ConsolidationHelper.getRawClass(type).isAssignableFrom(Set.class);
    }

    @Override
    protected Collection<?> createCollection() {
        return new HashSet<>();
    }
}
