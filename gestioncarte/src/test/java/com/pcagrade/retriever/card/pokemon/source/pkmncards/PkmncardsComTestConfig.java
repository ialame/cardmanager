package com.pcagrade.retriever.card.pokemon.source.pkmncards;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestConfig;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestProvider;
import com.pcagrade.retriever.card.pokemon.source.pkmncards.path.PkmncardsComSetPath;
import com.pcagrade.retriever.card.pokemon.source.pkmncards.path.PkmncardsComSetPathRepository;
import com.pcagrade.retriever.card.pokemon.source.pkmncards.path.PkmncardsComSetPathService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Optional;

@RetrieverTestConfiguration
@Import(PokemonSetTestConfig.class)
public class PkmncardsComTestConfig {

    private PkmncardsComSetPath mockPath(String path) {
        PkmncardsComSetPath source = new PkmncardsComSetPath();

        source.setId(1);
        source.setPath(path);
        return source;
    }

    @Bean
    public PkmncardsComSetPathRepository pkmncardsComSetPathRepository() {
        var repository = Mockito.mock(PkmncardsComSetPathRepository.class);

        Mockito.when(repository.findBySetId(PokemonSetTestProvider.SWSH_ID)).thenReturn(Optional.of(mockPath("https://pkmncards.com/set/sword-shield-promos/")));
        Mockito.when(repository.findBySetId(PokemonSetTestProvider.XY_ID)).thenReturn(Optional.of(mockPath("https://pkmncards.com/set/xy/")));
        Mockito.when(repository.findBySetId(PokemonSetTestProvider.LATIAS_HALF_DECK_ID)).thenReturn(Optional.of(mockPath("https://pkmncards.com/set/xy-trainer-kit-latias/")));
        return repository;
    }

    @Bean
    public PkmncardsComSetPathService pkmncardsComSetPathService() {
        return new PkmncardsComSetPathService();
    }

    @Bean
    public PkmncardsComParser pkmncardsComParser() {
        return new PkmncardsComParser();
    }

    @Bean
    public PkmncardsComService pkmncardsComService() {
        return new PkmncardsComService();
    }
}
