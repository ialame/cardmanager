package com.pcagrade.retriever.card.pokemon.translation;

import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.PokemonCardTestProvider;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestProvider;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction.BulbapediaExtractionService;
import com.pcagrade.retriever.card.pokemon.source.jcc.JCCPokemonTfService;
import com.pcagrade.retriever.card.pokemon.source.official.OfficialSiteTranslationService;
import com.pcagrade.retriever.card.pokemon.source.official.jp.JapaneseOfficialSiteService;
import com.pcagrade.retriever.card.pokemon.source.pkmncards.PkmncardsComService;
import com.pcagrade.retriever.card.pokemon.source.pokecardex.PokecardexComService;
import com.pcagrade.retriever.card.pokemon.source.wiki.PokemonWikiTranslatorFactory;
import com.pcagrade.mason.localization.Localization;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(IPokemonCardTranslatorFactoryTestConfig.class)
class IPokemonCardTranslatorFactoryShould {

	@Autowired(required = false)
	private List<IPokemonCardTranslatorFactory> translatorFactories;


	@ParameterizedTest
	@MethodSource("provideFrenchFactories")
	void translateCard_to_french(IPokemonCardTranslatorFactory factory) {
		var card = PokemonCardTestProvider.venusaur();
		var value = factory.createTranslator(PokemonSetTestProvider.xy1(), card).translate(card.getTranslations().get(Localization.USA), Localization.FRANCE);

		assertThat(value).isNotEmpty()
				.hasValueSatisfying(c -> assertThat(c.getName()).containsIgnoringCase("Florizarre"));
	}

	@ParameterizedTest
	@MethodSource("provideGermanFactories")
	void translateCard_to_german(IPokemonCardTranslatorFactory factory) {
		var card = PokemonCardTestProvider.venusaur();
		var value = factory.createTranslator(PokemonSetTestProvider.xy1(), card).translate(card.getTranslations().get(Localization.USA), Localization.GERMANY);

		assertThat(value).isNotEmpty()
				.hasValueSatisfying(c -> assertThat(c.getName()).containsIgnoringCase("Bisaflor"));
	}

	@ParameterizedTest
	@MethodSource("provideSpanishFactories")
	void translateCard_to_spanish(IPokemonCardTranslatorFactory factory) {
		var card = PokemonCardTestProvider.venusaur();
		var value = factory.createTranslator(PokemonSetTestProvider.xy1(), card).translate(card.getTranslations().get(Localization.USA), Localization.SPAIN);

		assertThat(value).isNotEmpty()
				.hasValueSatisfying(c -> assertThat(c.getName()).containsIgnoringCase("Venusaur"));
	}

	@ParameterizedTest
	@MethodSource("provideJapaneseFactories")
	void translateCard_to_japanese(IPokemonCardTranslatorFactory factory) {
		var card = PokemonCardTestProvider.bulbasaurDTO();
		var value = factory.createTranslator(PokemonSetTestProvider.pokemonGOjpDTO(), card).translate(card.getTranslations().get(Localization.JAPAN), Localization.JAPAN);

		assertThat(value).isNotEmpty()
				.hasValueSatisfying(c -> assertThat(c.getOriginalName()).containsIgnoringCase("フシギダネ"));
	}

	@Test
	void translateCard_to_spanish_with_trainer() {
		var card = PokemonCardTestProvider.ProfessorsResearch();
		var value = getWikiTranslatorFactory(Localization.SPAIN)
				.createTranslator(PokemonSetTestProvider.pokemonGOusDTO(), card).translate(card.getTranslations().get(Localization.USA), Localization.SPAIN);

		assertThat(value).isNotEmpty()
				.hasValueSatisfying(c -> assertThat(c.getName()).matches(name -> StringUtils.containsNone(name, "{}[]()"), "Name should not contain braces, brackets or parentheses"))
				.hasValueSatisfying(c -> assertThat(c.getLabelName()).matches(name -> StringUtils.containsNone(name, "{}[]()"), "LabelName should not contain braces, brackets or parentheses"));
	}

	@Nonnull
	private IPokemonCardTranslatorFactory getWikiTranslatorFactory(Localization localization) {
		return translatorFactories.stream()
				.filter(f -> f instanceof PokemonWikiTranslatorFactory wikiFactory && wikiFactory.getParser().canTranslateTo(localization))
				.findFirst()
				.orElseThrow();
	}

	@ParameterizedTest
	@MethodSource("provideFactories")
	void empty_with_invalidCard(IPokemonCardTranslatorFactory factory) {
		var card = new PokemonCardDTO();
		var value = factory.createTranslator(PokemonSetTestProvider.xy1(), card).translate(card.getTranslations().get(Localization.USA), Localization.FRANCE);

		assertThat(value).isEmpty();
	}

	@ParameterizedTest
	@MethodSource("provideFactories")
	void hasName(IPokemonCardTranslatorFactory factory) {
		var name = factory.createTranslator(PokemonSetTestProvider.xy1(), new PokemonCardDTO()).getName();

		assertThat(name).isNotEmpty();
	}

	private Stream<Arguments> provideFactories() {
		return translatorFactories.stream().map(Arguments::of);
	}

	private Stream<Arguments> provideFrenchFactories() {
		return translatorFactories.stream()
				.filter(f -> (!(f instanceof PokemonWikiTranslatorFactory wikiFactory) || wikiFactory.getParser().canTranslateTo(Localization.FRANCE)) && !(f instanceof JapaneseOfficialSiteService || f instanceof OfficialSiteTranslationService || f instanceof PkmncardsComService || f instanceof JCCPokemonTfService /* TODO remove JCC later */))
				.map(Arguments::of);
	}

	private Stream<Arguments> provideGermanFactories() {
		return translatorFactories.stream()
				.filter(f -> (!(f instanceof PokemonWikiTranslatorFactory wikiFactory) || wikiFactory.getParser().canTranslateTo(Localization.GERMANY)) && !(f instanceof JapaneseOfficialSiteService || f instanceof OfficialSiteTranslationService || f instanceof PkmncardsComService || f instanceof JCCPokemonTfService || f instanceof PokecardexComService))
				.map(Arguments::of);
	}

	private Stream<Arguments> provideSpanishFactories() {
		return translatorFactories.stream()
				.filter(f -> (!(f instanceof PokemonWikiTranslatorFactory wikiFactory) || wikiFactory.getParser().canTranslateTo(Localization.SPAIN)) && !(f instanceof JapaneseOfficialSiteService || f instanceof OfficialSiteTranslationService || f instanceof PkmncardsComService || f instanceof JCCPokemonTfService || f instanceof PokecardexComService))
				.map(Arguments::of);
	}

	private Stream<Arguments> provideJapaneseFactories() {
		return translatorFactories.stream()
				.filter(f -> f instanceof JapaneseOfficialSiteService || f instanceof BulbapediaExtractionService)
				.map(Arguments::of);
	}
}
