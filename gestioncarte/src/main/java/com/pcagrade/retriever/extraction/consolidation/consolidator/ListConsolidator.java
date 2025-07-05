package com.pcagrade.retriever.extraction.consolidation.consolidator;

import com.pcagrade.retriever.extraction.consolidation.ConsolidationHelper;
import org.springframework.core.ResolvableType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListConsolidator extends AbstractIterableConsolidator {

    @Override
    public boolean canConsolidate(ResolvableType type) {
        return ConsolidationHelper.getRawClass(type).isAssignableFrom(List.class);
    }

    @Override
    protected Collection<?> createCollection() {
        return new ArrayList<>();
    }
}
