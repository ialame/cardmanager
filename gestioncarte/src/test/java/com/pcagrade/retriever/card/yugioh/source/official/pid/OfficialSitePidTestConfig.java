package com.pcagrade.retriever.card.yugioh.source.official.pid;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetRepositoryTestConfig;
import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetTestProvider;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.ulid.UlidHelper;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;

@RetrieverTestConfiguration
@Import(YuGiOhSetRepositoryTestConfig.class)
public class OfficialSitePidTestConfig {

    private static OfficialSitePid amazingDefendersUS() {
        var pid = new OfficialSitePid();

        pid.setId(1);
        pid.setPid("2000001225000");
        pid.setLocalization(Localization.USA);
        pid.setSet(YuGiOhSetTestProvider.amazingDefenders());
        return pid;
    }

    public static OfficialSitePid amazingDefendersFR() {
        var pid = new OfficialSitePid();

        pid.setId(2);
        pid.setPid("2000001225000");
        pid.setLocalization(Localization.FRANCE);
        pid.setSet(YuGiOhSetTestProvider.amazingDefenders());
        return pid;
    }

    @Bean
    public OfficialSitePidRepository officialSitePidRepository() {
        var list = List.of(
                amazingDefendersUS(),
                amazingDefendersFR()
        );

        var repository = RetrieverTestUtils.mockRepository(OfficialSitePidRepository.class, list, OfficialSitePid::getId);

        Mockito.when(repository.findAllBySetId(Mockito.any(Ulid.class))).then(invocation -> {
            var setId = invocation.getArgument(0, Ulid.class);

            return list.stream()
                    .filter(pid -> UlidHelper.equals(pid.getSet().getId(), setId))
                    .toList();
        });
        return repository;
    }

    @Bean
    public OfficialSitePidMapper officialSitePidMapper() {
        return new OfficialSitePidMapperImpl();
    }

    @Bean
    public OfficialSitePidService officialSitePidService() {
        return new OfficialSitePidService();
    }

}
