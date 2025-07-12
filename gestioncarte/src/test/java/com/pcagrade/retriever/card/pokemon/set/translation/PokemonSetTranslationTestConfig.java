package com.pcagrade.retriever.card.pokemon.set.translation;

import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import org.springframework.context.annotation.Bean;

@RetrieverTestConfiguration
public class PokemonSetTranslationTestConfig {

	@Bean
	public PokemonSetTranslationRepository pokemonSetTranslationRepository() {
		return RetrieverTestUtils.mockRepository(PokemonSetTranslationRepository.class);
	}

	@Bean
	public PokemonSetTranslationMapper pokemonSetTranslationMapper() {
		return new PokemonSetTranslationMapperImpl();
	}

}
