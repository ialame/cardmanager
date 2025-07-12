package com.pcagrade.retriever.card.pokemon.translation;

import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.pokemon.PokemonCardTestProvider;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestProvider;
import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(PokemonCardTranslationServiceTestConfig.class)
class PokemonCardTranslationServiceShould {

	@Autowired
	PokemonCardTranslationService pokemonCardTranslationService;

	@Test
	void getCardTranslations() {
		var card = PokemonCardTestProvider.venusaur();
		var set = PokemonSetTestProvider.xy1();

		var translations = pokemonCardTranslationService.getCardTranslations(set, card, Localization.USA);

		assertThat(translations)
				.isNotEmpty()
				.anyMatch(translation -> "Florizarre".equals(translation.translation().getName()));
	}

	@Test
	void getTranslations() {
		var card = PokemonCardTestProvider.venusaur();
		var translations = pokemonCardTranslationService.getTranslations(card, Localization.USA);

		assertThat(translations).hasSizeGreaterThanOrEqualTo(4);
	}

	@Test
	void translateCard() {
		var t1 = new PokemonCardTranslationDTO();

		t1.setName("Venusaur");
		t1.setNumber("001");
		t1.setLocalization(Localization.USA);
		var t2 = new PokemonCardTranslationDTO();

		t2.setName("Florizarre");
		t2.setNumber("002");
		t2.setLocalization(Localization.USA);

		var translations = pokemonCardTranslationService.translateCard(PokemonCardTestProvider.venusaur(), List.of(
				new SourcedPokemonCardTranslationDTO(Localization.USA, "test1", 10, "", t1),
				new SourcedPokemonCardTranslationDTO(Localization.USA, "test2", 1, "", t2)
		));

		assertThat(translations).hasSize(1)
				.allSatisfy((l, t) -> {
					assertThat(t.getName()).isEqualTo("Venusaur");
					assertThat(t.getNumber()).isEqualTo("001");
					assertThat(t.getLocalization()).isEqualTo(Localization.USA);
				});
	}
}
