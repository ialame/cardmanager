package com.pcagrade.retriever.card.pokemon.source.official.jp.source;

import com.pcagrade.retriever.TestUlidProvider;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestConfig;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestProvider;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;

@RetrieverTestConfiguration
@Import(PokemonSetTestConfig.class)
public class JapaneseOfficialSiteSourceTestConfig {

    private JapaneseOfficialSiteSource mockSource(int id, String pg) {
        JapaneseOfficialSiteSource source = new JapaneseOfficialSiteSource();

        source.setId(id);
        source.setPg(pg);
        return source;
    }

    @Bean
    public JapaneseOfficialSiteSourceRepository japaneseOfficialSiteSourceRepository() {
        var repository = Mockito.mock(JapaneseOfficialSiteSourceRepository.class);

        Mockito.when(repository.findAllBySetId(TestUlidProvider.ID_1)).thenReturn(List.of(mockSource(1, "851")));
        Mockito.when(repository.findAllBySetId(PokemonSetTestProvider.POKEMON_GO_JAPAN_ID)).thenReturn(List.of(mockSource(2, "861")));
        Mockito.when(repository.findAllBySetId(PokemonSetTestProvider.LUCARIO_VSTAR_STARTER_SET_ID)).thenReturn(List.of(mockSource(3, "854")));
        return repository;
    }

    @Bean
    public JapaneseOfficialSiteSourceService japaneseOfficialSiteSourceService() {
        return new JapaneseOfficialSiteSourceService();
    }
}
