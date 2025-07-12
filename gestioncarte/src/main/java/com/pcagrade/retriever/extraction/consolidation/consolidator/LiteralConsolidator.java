package com.pcagrade.retriever.extraction.consolidation.consolidator;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.extraction.consolidation.ConsolidationHelper;
import com.pcagrade.retriever.extraction.consolidation.ConsolidationService;
import com.pcagrade.retriever.extraction.consolidation.annotation.ConsolidationLiteral;
import com.pcagrade.retriever.extraction.consolidation.source.IConsolidationSource;
import org.springframework.core.ResolvableType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LiteralConsolidator implements IConsolidator {
    @Override
    public boolean canConsolidate(ResolvableType type) {
        var rawClass = ConsolidationHelper.getRawClass(type);

        return rawClass.isPrimitive() || rawClass.isEnum() || CharSequence.class.isAssignableFrom(rawClass) || Ulid.class.isAssignableFrom(rawClass) || LocalDate.class.isAssignableFrom(rawClass) || rawClass.isAnnotationPresent(ConsolidationLiteral.class);
    }

    @Override
    public Object consolidate(ResolvableType type, ConsolidationService service, List<? extends IConsolidationSource<?>> sources) {
        return sources.stream()
                .collect(Collectors.groupingBy(IConsolidationSource::getValue, Collectors.mapping(IConsolidationSource::getWeight, Collectors.summingInt(Integer::intValue)))).entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}
