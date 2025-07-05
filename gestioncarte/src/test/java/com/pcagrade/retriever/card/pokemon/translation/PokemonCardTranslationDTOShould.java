package com.pcagrade.retriever.card.pokemon.translation;

import com.pcagrade.retriever.annotation.RetrieverTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest
class PokemonCardTranslationDTOShould {

	@Test
	void createACopy() {
		var translation = PokemonCardTranslationTestProvider.venusaurDTO();

		assertThat(translation.copy())
				.isNotNull()
				.usingRecursiveComparison().isEqualTo(translation);
	}
}
