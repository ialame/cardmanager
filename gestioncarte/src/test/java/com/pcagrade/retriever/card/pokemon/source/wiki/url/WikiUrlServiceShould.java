package com.pcagrade.retriever.card.pokemon.source.wiki.url;

import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestProvider;
import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(WikiUrlTestConfig.class)
class WikiUrlServiceShould {

    @Autowired
    WikiUrlRepository wikiUrlRepository;

    @Autowired
    WikiUrlService wikiUrlService;

    @Test
    void getPath() {
        var urls = wikiUrlService.getUrls(PokemonSetTestProvider.XY_ID, Localization.FRANCE);

        assertThat(urls).hasSize(1);
        assertThat(urls.get(0).url()).isEqualTo("https://www.pokepedia.fr/XY_(JCC)");
    }

    @SuppressWarnings("unchecked")
    @Test
    void setPath() {
        Mockito.clearInvocations(wikiUrlRepository);

        wikiUrlService.setUrls(PokemonSetTestProvider.XY_ID, Localization.FRANCE, List.of(new WikiUrlDTO("test_url", "test_selector")));

        ArgumentCaptor<List<WikiUrl>> deleteCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<WikiUrl> saveCaptor = ArgumentCaptor.forClass(WikiUrl.class);

        Mockito.verify(wikiUrlRepository, Mockito.times(1)).deleteAll(deleteCaptor.capture());

        var deleted = deleteCaptor.getValue();

        assertThat(deleted).hasSize(1);
        assertThat(deleted.get(0).getSet().getId()).isEqualTo(PokemonSetTestProvider.XY_ID);
        assertThat(deleted.get(0).getLocalization()).isEqualTo(Localization.FRANCE);
        assertThat(deleted.get(0).getUrl()).isEqualTo("https://www.pokepedia.fr/XY_(JCC)");

        Mockito.verify(wikiUrlRepository, Mockito.times(1)).save(saveCaptor.capture());

        var saved = saveCaptor.getValue();

        assertThat(saved.getSet().getId()).isEqualTo(PokemonSetTestProvider.XY_ID);
        assertThat(saved.getLocalization()).isEqualTo(Localization.FRANCE);
        assertThat(saved.getUrl()).isEqualTo("test_url");
        assertThat(saved.getCssSelector()).isEqualTo("test_selector");
    }

}
