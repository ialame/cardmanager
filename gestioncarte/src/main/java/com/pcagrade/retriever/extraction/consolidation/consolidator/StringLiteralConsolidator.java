package com.pcagrade.retriever.extraction.consolidation.consolidator;

import com.pcagrade.retriever.extraction.consolidation.ConsolidationHelper;
import com.pcagrade.retriever.extraction.consolidation.ConsolidationService;
import com.pcagrade.retriever.extraction.consolidation.source.IConsolidationSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ResolvableType;

import java.util.List;

public class StringLiteralConsolidator extends LiteralConsolidator {
    @Override
    public boolean canConsolidate(ResolvableType type) {
        return CharSequence.class.isAssignableFrom(ConsolidationHelper.getRawClass(type));
    }

    @Override
    public Object consolidate(ResolvableType type, ConsolidationService service, List<? extends IConsolidationSource<?>> sources) {
        return super.consolidate(type, service, sources.stream()
                .filter(source -> StringUtils.isNotBlank((CharSequence) source.getValue()))
                .toList());
    }
}
