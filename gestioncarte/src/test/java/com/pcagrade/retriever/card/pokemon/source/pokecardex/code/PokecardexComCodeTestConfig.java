package com.pcagrade.retriever.card.pokemon.source.pokecardex.code;

import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestConfig;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestProvider;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Optional;

@RetrieverTestConfiguration
@Import(PokemonSetTestConfig.class)
public class PokecardexComCodeTestConfig {

    private PokecardexSetCode xy() {
        var code = new PokecardexSetCode();

        code.setId(1);
        code.setSet(PokemonSetTestProvider.xy());
        code.setCode("XY");
        return code;
    }

    @Bean
    public PokecardexSetCodeRepository pokecardexSetCodeRepository() {
        var repository = RetrieverTestUtils.mockRepository(PokecardexSetCodeRepository.class);

        Mockito.when(repository.findBySetId(PokemonSetTestProvider.XY_ID)).thenReturn(Optional.of(xy()));
        return repository;
    }

    @Bean
    public PokecardexSetCodeService pokecardexSetCodeService() {
        return new PokecardexSetCodeService();
    }

}
