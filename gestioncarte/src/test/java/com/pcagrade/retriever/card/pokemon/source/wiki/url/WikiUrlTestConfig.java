package com.pcagrade.retriever.card.pokemon.source.wiki.url;

import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.feature.FeatureTestConfig;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestConfig;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestProvider;
import com.pcagrade.mason.localization.Localization;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;

@RetrieverTestConfiguration
@Import({ PokemonSetTestConfig.class, FeatureTestConfig.class })
public class WikiUrlTestConfig {

    private WikiUrl xyFrance() {
        WikiUrl url = new WikiUrl();

        url.setId(1);
        url.setUrl("https://www.pokepedia.fr/XY_(JCC)");
        url.setLocalization(Localization.FRANCE);
        url.setSet(PokemonSetTestProvider.xy());
        return url;
    }

    private WikiUrl xyGermany() {
        WikiUrl url = new WikiUrl();

        url.setId(2);
        url.setUrl("https://www.pokewiki.de/XY_(TCG)");
        url.setLocalization(Localization.GERMANY);
        url.setSet(PokemonSetTestProvider.xy());
        return url;
    }

    private WikiUrl xySpain() {
        WikiUrl url = new WikiUrl();

        url.setId(3);
        url.setUrl("https://www.wikidex.net/wiki/XY_(TCG):_XY");
        url.setLocalization(Localization.SPAIN);
        url.setSet(PokemonSetTestProvider.xy());
        return url;
    }

    private WikiUrl pokemonGoSpain() {
        WikiUrl url = new WikiUrl();

        url.setId(4);
        url.setUrl("https://www.wikidex.net/wiki/Pok%C3%A9mon_GO_(TCG)");
        url.setLocalization(Localization.SPAIN);
        url.setSet(PokemonSetTestProvider.pokemonGOus());
        return url;
    }

    @Bean
    public WikiUrlRepository wikiUrlRepository() {
        var repository = RetrieverTestUtils.mockRepository(WikiUrlRepository.class);

        Mockito.when(repository.findAllBySetIdAndLocalization(PokemonSetTestProvider.XY_ID, Localization.FRANCE)).thenReturn(List.of(xyFrance()));
        Mockito.when(repository.findAllBySetIdAndLocalization(PokemonSetTestProvider.XY_ID, Localization.GERMANY)).thenReturn(List.of(xyGermany()));
        Mockito.when(repository.findAllBySetIdAndLocalization(PokemonSetTestProvider.XY_ID, Localization.SPAIN)).thenReturn(List.of(xySpain()));
        Mockito.when(repository.findAllBySetIdAndLocalization(PokemonSetTestProvider.POKEMON_GO_USA_ID, Localization.SPAIN)).thenReturn(List.of(pokemonGoSpain()));
        return repository;
    }

    @Bean
    public WikiUrlMapper wikiUrlMapper() {
        return new WikiUrlMapperImpl();
    }

    @Bean
    public WikiUrlService wikiUrlService() {
        return new WikiUrlService();
    }

}
