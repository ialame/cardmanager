package com.pcagrade.retriever.card.pokemon.source.official.path;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestConfig;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestProvider;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;

@RetrieverTestConfiguration
@Import(PokemonSetTestConfig.class)
public class OfficialSiteSetPathTestConfig {

    private static OfficialSiteSetPath xy1() {
        var path = new OfficialSiteSetPath();

        path.setId(1);
        path.setPath("xy-series/xy1");
        path.setSet(PokemonSetTestProvider.xy());
        return path;
    }


    private static OfficialSiteSetPath swsh10() {
        var path = new OfficialSiteSetPath();

        path.setId(2);
        path.setPath("ss-series/swsh10");
        path.setSet(PokemonSetTestProvider.astralRadiance());
        return path;
    }

    private static OfficialSiteSetPath swsh10tg() {
        var path = new OfficialSiteSetPath();

        path.setId(3);
        path.setPath("ss-series/swsh10tg");
        path.setSet(PokemonSetTestProvider.astralRadiance());
        return path;
    }

    @Bean
    public OfficialSiteSetPathRepository officialSiteSetPathRepository() {
        var repository = Mockito.mock(OfficialSiteSetPathRepository.class);

        Mockito.when(repository.findAllBySetId(PokemonSetTestProvider.XY_ID)).thenReturn(List.of(xy1()));
        Mockito.when(repository.findAllBySetId(PokemonSetTestProvider.ASTRAL_RADIANCE_ID)).thenReturn(List.of(swsh10(), swsh10tg()));
        return repository;
    }

    @Bean
    public OfficialSiteSetPathService officialSiteSetPathService() {
        return new OfficialSiteSetPathService();
    }
}
