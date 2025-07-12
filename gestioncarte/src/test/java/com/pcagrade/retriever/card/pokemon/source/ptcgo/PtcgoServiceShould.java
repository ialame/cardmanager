package com.pcagrade.retriever.card.pokemon.source.ptcgo;

import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestProvider;
import com.pcagrade.mason.localization.Localization;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(PtcgoTestConfig.class)
class PtcgoServiceShould {

	@Autowired
	PtcgoService ptcgoService;

	@Test
	void getEnglishTranslation() {
		var list = ptcgoService.getSetTranslation(PokemonSetTestProvider.xy1(), Localization.USA);

		assertThat(list).isNotEmpty();
	}

	@Test
	void findBySetId_with_validId() {
		var ptcgoSets = ptcgoService.findBySetId(PokemonSetTestProvider.XY_ID);

		Assertions.assertThat(ptcgoSets)
				.isNotEmpty()
				.anyMatch(p -> "xy1".equals(p.getFileName()));
	}
}
