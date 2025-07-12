package com.pcagrade.retriever.extraction.consolidation.consolidator;

import com.pcagrade.retriever.extraction.consolidation.ConsolidationHelper;
import com.pcagrade.retriever.extraction.consolidation.ConsolidationService;
import com.pcagrade.retriever.extraction.consolidation.source.IConsolidationSource;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.ResolvableType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapConsolidator implements IConsolidator {

    @Override
    public boolean canConsolidate(ResolvableType type) {
        return ConsolidationHelper.getRawClass(type).isAssignableFrom(Map.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object consolidate(ResolvableType type, ConsolidationService service, List<? extends IConsolidationSource<?>> sources) {
        var valueType = type.getGeneric(1);

        return sources.stream()
                .<Pair<Object, IConsolidationSource<?>>>mapMulti((s, downstream) -> {
                    var map = (Map<Object, Object>) s.getValue();

                    map.forEach((k, v) -> downstream.accept(Pair.of(k, s.with(v))));
                })
                .collect(Collectors.groupingBy(Pair::getKey, Collectors.collectingAndThen(Collectors.toList(), l -> {
                    var values = l.stream()
                            .map(Pair::getValue)
                            .toList();

                    return service.consolidateValue(valueType, values);
                })));
    }
}
