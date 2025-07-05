package com.pcagrade.retriever.card.pokemon.set;

import com.pcagrade.retriever.annotation.RetrieverTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(PokemonSetTestConfig.class)
class PokemonSetServiceShould {

    @Autowired
    PokemonSetService pokemonSetService;

    @Autowired
    PokemonSetRepository pokemonSetRepository;

    @Test
    void findSet() {
        var set = pokemonSetService.findSet(PokemonSetTestProvider.COLLECTION_X_ID);

        assertThat(set).isNotEmpty();
    }

    @Test
    void findSet_return_empty_with_nullId() {
        var set = pokemonSetService.findSet(null);

        assertThat(set).isEmpty();
    }

    @Test
    void getExistingIdPca() {
        var idPca = pokemonSetService.getIdPca(PokemonSetTestProvider.COLLECTION_X_ID);

        assertThat(idPca)
                .isEqualTo(110);
    }

    @Test
    void build_idPca_if_notFound() {
        Mockito.clearInvocations(pokemonSetRepository);
        var idPca = pokemonSetService.getIdPca(PokemonSetTestProvider.XY_ID);

        Mockito.verify(pokemonSetRepository, Mockito.times(1)).existsByIdPca(idPca);
        Mockito.verify(pokemonSetRepository, Mockito.times(1)).save(Mockito.any(PokemonSet.class));
        assertThat(idPca)
                .isEqualTo(810);
    }

}
