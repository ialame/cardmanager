package com.pcagrade.retriever.card.pokemon.translation.translator;

import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Optional;

@RetrieverTestConfiguration
public class PokemonCardTranslatorServiceTestConfig {

    private PokemonCardTranslator ptcgoData() {
        var translator = new PokemonCardTranslator();

        translator.setId(1);
        translator.setCode("ptcgo-data");
        translator.setWeight(1);
        return translator;
    }

    private PokemonCardTranslator pcaTranslationDictionary() {
        var translator = new PokemonCardTranslator();

        translator.setId(2);
        translator.setCode("pca-translation-dictionary");
        translator.setWeight(2);
        return translator;
    }

    @Bean
    public PokemonCardTranslatorRepository pokemonCardTranslatorRepository() {
        var repository = RetrieverTestUtils.mockRepository(PokemonCardTranslatorRepository.class);

        Mockito.when(repository.findByCode("ptcgo-data")).thenReturn(Optional.of(ptcgoData()));
        Mockito.when(repository.findByCode("pca-translation-dictionary")).thenReturn(Optional.of(pcaTranslationDictionary()));
        Mockito.when(repository.findAll()).thenReturn(List.of(ptcgoData(), pcaTranslationDictionary()));
        return repository;
    }

    @Bean
    public PokemonCardTranslatorMapper pokemonCardTranslatorMapper() {
        return new PokemonCardTranslatorMapperImpl();
    }

    @Bean
    public PokemonCardTranslatorService pokemonCardTranslatorService() {
        return new PokemonCardTranslatorService();
    }


}
