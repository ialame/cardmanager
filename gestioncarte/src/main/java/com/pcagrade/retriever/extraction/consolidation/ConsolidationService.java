package com.pcagrade.retriever.extraction.consolidation;

import com.pcagrade.retriever.extraction.consolidation.consolidator.IConsolidator;
import com.pcagrade.retriever.extraction.consolidation.source.IConsolidationSource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.ResolvableType;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;

@SuppressWarnings("unchecked")
public class ConsolidationService {

    private final List<IConsolidator> consolidators;

    public ConsolidationService(List<IConsolidator> consolidators) {
        this.consolidators = consolidators;
    }

    @Nullable
    public <T> T consolidate(@Nonnull Type type, @Nonnull List<? extends IConsolidationSource<T>> sources) {
        try {
            return consolidateValue(ResolvableType.forType(type), sources);
        } catch (Exception e) {
            throw new ConsolidationException("Failed to consolidate " + type, e);
        }
    }

    @Nullable
    public <T> T consolidateValue(ResolvableType type, List<? extends IConsolidationSource<?>> sources) {
        return (T) doConsolidateValue(type, sources);
    }

    @Nullable
    private Object doConsolidateValue(ResolvableType type, List<? extends IConsolidationSource<?>> sources) {
        if (CollectionUtils.isEmpty(sources)) {
            return null;
        } else if (sources.size() == 1) {
            return sources.iterator().next().getValue();
        }

        for (var consolidator : consolidators) {
            if (consolidator.canConsolidate(type)) {
                return consolidator.consolidate(type, this, sources);
            }
        }
        throw new ConsolidationException("No consolidator found for " + type);
    }
}
