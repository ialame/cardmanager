package com.pcagrade.retriever.extraction.consolidation.consolidator;

import com.pcagrade.retriever.extraction.consolidation.ConsolidationService;
import com.pcagrade.retriever.extraction.consolidation.source.IConsolidationSource;
import org.springframework.core.ResolvableType;

import java.util.List;

public interface IConsolidator {

    boolean canConsolidate(ResolvableType type);

    Object consolidate(ResolvableType type, ConsolidationService service, List<? extends IConsolidationSource<?>> sources);

}
