package com.pcagrade.retriever.card.pokemon.source.ptcgo;

import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.feature.FeatureTestConfig;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestConfig;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestProvider;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;

@RetrieverTestConfiguration
@Import({FeatureTestConfig.class, PokemonSetTestConfig.class})
public class PtcgoTestConfig {

	private PtcgoSet xy9() {
		var ptcgoSet = new PtcgoSet();

		ptcgoSet.setId(2);
		ptcgoSet.setFileName("xy1");
		return ptcgoSet;
	}

	@Bean
	public PtcgoSetRepository ptcgoSetRepository() {
		var ptcgoSetRepository = RetrieverTestUtils.mockRepository(PtcgoSetRepository.class);

		Mockito.when(ptcgoSetRepository.findAllBySetId(PokemonSetTestProvider.XY_ID)).thenReturn(List.of(xy9()));
		return ptcgoSetRepository;
	}

	@Bean
	public PtcgoSetMapper ptcgoSetMapper() {
		return new PtcgoSetMapperImpl();
	}

	@Bean
	public PtcgoService ptcgoService() {
		return new PtcgoService();
	}

}
