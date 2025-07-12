package com.pcagrade.retriever.card.yugioh.source.official.pid;

import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetTestProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(OfficialSitePidTestConfig.class)
class OfficialSitePidServiceShould {

    @Autowired
    private OfficialSitePidService officialSitePidService;

    @Test
    void getPids() {
        var pids = officialSitePidService.getPids(YuGiOhSetTestProvider.AMAZING_DEFENDERS_ID);

        assertThat(pids).isNotEmpty().hasSize(2);
    }

}
