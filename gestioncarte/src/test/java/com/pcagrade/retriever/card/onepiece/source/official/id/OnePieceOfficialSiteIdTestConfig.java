package com.pcagrade.retriever.card.onepiece.source.official.id;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.onepiece.set.OnePieceSetTestConfig;
import com.pcagrade.mason.ulid.UlidHelper;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

@RetrieverTestConfiguration
public class OnePieceOfficialSiteIdTestConfig {

    @Bean
    public OnePieceOfficialSiteIdRepository onePieceOfficialSiteIdRepository() {
        var list = OnePieceSetTestConfig.LIST.stream()
                .<OnePieceOfficialSiteId>mapMulti((set, downstream) -> set.getOfficialSiteIds().forEach(downstream))
                .toList();

        var repository = RetrieverTestUtils.mockRepository(OnePieceOfficialSiteIdRepository.class, list, OnePieceOfficialSiteId::getId);

        Mockito.when(repository.findAllBySetId(Mockito.any(Ulid.class))).then(invocation -> {
            var setId = invocation.getArgument(0, Ulid.class);

            return list.stream()
                    .filter(id -> UlidHelper.equals(id.getSet().getId(), setId))
                    .toList();
        });
        return repository;
    }

    @Bean
    public OnePieceOfficialSiteIdMapper onePieceOfficialSiteIdMapper() {
        return new OnePieceOfficialSiteIdMapperImpl();
    }

    @Bean
    public OnePieceOfficialSiteIdService onePieceOfficialSiteIdService() {
        return new OnePieceOfficialSiteIdService();
    }
}
