package com.pcagrade.retriever.extraction.consolidation.consolidator;

import com.pcagrade.retriever.extraction.consolidation.ConsolidationHelper;
import com.pcagrade.retriever.extraction.consolidation.ConsolidationService;
import com.pcagrade.retriever.extraction.consolidation.source.IConsolidationSource;
import org.springframework.core.ResolvableType;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractIterableConsolidator implements IConsolidator {
    @Override
    public boolean canConsolidate(ResolvableType type) {
        return ConsolidationHelper.getRawClass(type).isAssignableFrom(Iterable.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object consolidate(ResolvableType type, ConsolidationService service, List<? extends IConsolidationSource<?>> sources) {
        var valueType = type.getGeneric(0);

        return sources.stream()
                .mapMulti((s, downstream) -> {
                    var list = (Iterable<Object>) s.getValue();

                    if (list != null) {
                        list.forEach(v -> downstream.accept(service.consolidateValue(valueType, List.of(s.with(v)))));
                    }
                })
                .distinct()
                .collect(Collectors.toCollection(() -> (Collection<Object>) createCollection()));
    }

    protected abstract Collection<?> createCollection();
}
