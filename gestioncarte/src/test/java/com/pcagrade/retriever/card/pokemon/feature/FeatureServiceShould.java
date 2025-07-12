package com.pcagrade.retriever.card.pokemon.feature;

import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.pokemon.PokemonCardTestProvider;
import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(FeatureTestConfig.class)
class FeatureServiceShould {

	@Autowired
	FeatureService featireService;

	@Autowired
	FeatureRepository featureRepository;

	@Test
	void getInNameFeatures_contains_validValues() {
		List<FeatureDTO> list = featireService.getInNameFeatures();

		assertThat(list).isNotEmpty();
	}

	@Test
	void applyFeaturePatterns() {
		var card = PokemonCardTestProvider.venusaurEX();
		var translation= card.getTranslations().get(Localization.USA);
		featireService.applyFeaturePatterns(card, translation, "bulbapedia");

		assertThat(translation.getName()).isEqualTo("Venusaur EX");
		assertThat(translation.getLabelName()).isEqualTo("Venusaur EX test");
	}

	@Test
	void dont_applyFeaturePatterns_when_thereIsNoFeatures() {
		var card = PokemonCardTestProvider.venusaur();
		var translation= card.getTranslations().get(Localization.USA);
		featireService.applyFeaturePatterns(card, translation, "bulbapedia");

		assertThat(translation.getName()).isEqualTo("Venusaur");
		assertThat(translation.getLabelName()).isEqualTo("Venusaur");
	}

	@Test
	void save() {
		Mockito.clearInvocations(featureRepository);

		featireService.saveFeature(FeatureTestProvider.exDTO());
		Mockito.verify(featureRepository).save(Mockito.any());
	}

	@Test
	void findById_return_empty_with_nullId() {
		var feature = featireService.findById(null);

		assertThat(feature).isEmpty();
	}

	@Test
	void findFeature() {
		var feature = featireService.findFeature("wiki-fr-pokepedia", "-ex", "Pok%C3%A9mon-ex");

		assertThat(feature).isNotNull()
				.satisfies(f -> assertThat(f.getId()).isEqualTo(FeatureTestProvider.EX_NON_TERA_ID));
	}

}
