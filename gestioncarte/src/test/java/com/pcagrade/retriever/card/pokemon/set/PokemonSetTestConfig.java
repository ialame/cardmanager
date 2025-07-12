package com.pcagrade.retriever.card.pokemon.set;

import com.pcagrade.mason.jpa.revision.message.RevisionMessageConfiguration;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.PokemonFieldMappingServiceTestConfig;
import com.pcagrade.retriever.card.pokemon.serie.PokemonSerieTestConfig;
import com.pcagrade.retriever.card.pokemon.set.extraction.status.PokemonSetExtractionStatusMapper;
import com.pcagrade.retriever.card.pokemon.set.extraction.status.PokemonSetExtractionStatusMapperImpl;
import com.pcagrade.retriever.card.pokemon.set.translation.PokemonSetTranslationTestConfig;
import com.pcagrade.retriever.file.SharedFileServiceTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({RevisionMessageConfiguration.class, PokemonSerieTestConfig.class, SharedFileServiceTestConfig.class, PokemonSetRepositoryTestConfig.class, PokemonSetTranslationTestConfig.class, PokemonFieldMappingServiceTestConfig.class})
public class PokemonSetTestConfig {

	@Bean
	public PokemonSetExtractionStatusMapper pokemonSetExtractionStatusMapper() {
		return new PokemonSetExtractionStatusMapperImpl();
	}

	@Bean
	public PokemonSetMapper pokemonSetMapper() {
		return new PokemonSetMapperImpl();
	}

	@Bean
	public PokemonSetService pokemonSetService() {
		return new PokemonSetService();
	}

}
