package com.pcagrade.retriever.card.yugioh.set.extraction;

import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetDTO;
import com.pcagrade.retriever.extraction.consolidation.source.INamedConsolidationSource;

import java.util.List;

public interface IYuGiOhSetSourceService {
    List<INamedConsolidationSource<YuGiOhSetDTO>> getAllSets();
}
