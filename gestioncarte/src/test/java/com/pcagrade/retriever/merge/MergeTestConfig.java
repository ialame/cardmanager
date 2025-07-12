package com.pcagrade.retriever.merge;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.revision.message.RevisionMessageService;
import com.pcagrade.mason.ulid.UlidHelper;
import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.CardTestConfig;
import com.pcagrade.retriever.merge.history.MergeHistoryRepository;
import com.pcagrade.retriever.merge.history.MergeHistoryService;
import com.pcagrade.retriever.merge.history.MergeHistoryTestProvider;
import org.apache.commons.lang3.StringUtils;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({CardTestConfig.class, RevisionMessageService.class})
public class MergeTestConfig {

    @Bean
    public MergeHistoryRepository mergeHistoryRepository() {
        var cardMergeHistoryRepository = RetrieverTestUtils.mockRepository(MergeHistoryRepository.class);

        Mockito.when(cardMergeHistoryRepository.findAllByTableAndTo(Mockito.anyString(), Mockito.any(Ulid.class))).then(i -> {
            var table = i.getArgument(0, String.class);
            var id = i.getArgument(1, Ulid.class);

            return MergeHistoryTestProvider.HISTORY.stream()
                    .filter(h -> StringUtils.equals(h.getTable(), table) && UlidHelper.equals(h.getTo(), id))
                    .toList();
        });
        Mockito.when(cardMergeHistoryRepository.getLastRevision()).thenReturn(3);
        return cardMergeHistoryRepository;
    }

    @Bean
    public MergeHistoryService mergeHistoryService() {
        return new MergeHistoryService();
    }
}
