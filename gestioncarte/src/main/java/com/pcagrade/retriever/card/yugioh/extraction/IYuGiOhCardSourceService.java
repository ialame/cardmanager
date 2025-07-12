package com.pcagrade.retriever.card.yugioh.extraction;

import com.pcagrade.retriever.card.yugioh.YuGiOhCardDTO;
import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetDTO;
import com.pcagrade.retriever.extraction.consolidation.source.INamedConsolidationSource;
import com.pcagrade.retriever.progress.IProgressTracker;
import org.springframework.lang.Nullable;

import java.util.List;

public interface IYuGiOhCardSourceService {

    default List<INamedConsolidationSource<YuGiOhCardDTO>> extractCards(YuGiOhSetDTO set) {
        return extractCards(set, null);
    }

    List<INamedConsolidationSource<YuGiOhCardDTO>> extractCards(YuGiOhSetDTO set, @Nullable IProgressTracker tracker);
}
