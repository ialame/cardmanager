package com.pcagrade.retriever.card.pokemon.serie;

import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.serie.translation.PokemonSerieTranslationMapper;
import com.pcagrade.retriever.card.pokemon.serie.translation.PokemonSerieTranslationMapperImpl;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@RetrieverTestConfiguration
public class PokemonSerieTestConfig {

	@Bean
	public PokemonSerieRepository pokemonSerieRepository() {
		var repository = RetrieverTestUtils.mockRepository(PokemonSerieRepository.class);

		Mockito.when(repository.findById(PokemonSerieTestProvider.XY_ID)).thenReturn(Optional.of(PokemonSerieTestProvider.xy()));
		return repository;
	}

	@Bean
	public PokemonSerieTranslationMapper pokemonSerieTranslationMapper() {
		return new PokemonSerieTranslationMapperImpl();
	}

	@Bean
	public PokemonSerieMapper pokemonSerieMapper() {
		return new PokemonSerieMapperImpl();
	}

	@Bean
	public PokemonSerieService pokemonSerieService() {
		return new PokemonSerieService();
	}

}
