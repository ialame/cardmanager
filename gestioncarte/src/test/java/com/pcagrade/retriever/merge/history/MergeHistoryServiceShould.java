package com.pcagrade.retriever.merge.history;

import com.pcagrade.retriever.TestUlidProvider;
import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.merge.MergeTestConfig;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(MergeTestConfig.class)
class MergeHistoryServiceShould {

    @Autowired
    private MergeHistoryRepository mergeHistoryRepository;

    @Autowired
    private MergeHistoryService mergeHistoryService;

    @Test
    void getMergedIds() {
        var ids = mergeHistoryService.getMergedIds("pokemon_card", TestUlidProvider.ID_1);

        assertThat(ids).isNotEmpty().doesNotContainNull();
    }

    @Test
    void getMergedIds_should_returnEmpty_with_nullId() {
        var ids = mergeHistoryService.getMergedIds("pokemon_card", null);

        assertThat(ids).isEmpty();
    }

    @Test
    void addMergeHistory() {
        var argumentCaptor = ArgumentCaptor.forClass(MergeHistory.class);
        Mockito.clearInvocations(mergeHistoryRepository);
        mergeHistoryService.addMergeHistory("pokemon_card", TestUlidProvider.ID_1, TestUlidProvider.ID_2);

        Mockito.verify(mergeHistoryRepository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue())
                .isNotNull()
                .hasFieldOrPropertyWithValue("table", "pokemon_card")
                .hasFieldOrPropertyWithValue("from", TestUlidProvider.ID_1)
                .hasFieldOrPropertyWithValue("to", TestUlidProvider.ID_2)
                .hasFieldOrPropertyWithValue("revision", 3);
    }
}
