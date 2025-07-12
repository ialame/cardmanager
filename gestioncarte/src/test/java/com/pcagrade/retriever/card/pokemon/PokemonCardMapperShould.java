package com.pcagrade.retriever.card.pokemon;

import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.pokemon.bracket.BracketDTO;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.BulbapediaPokemonCard;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.parser.IBulbapediaParser;
import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(PokemonCardMapperTestConfig.class)
class PokemonCardMapperShould {

	private static final BiPredicate<BracketDTO, String> BRACKET_PREDICATE = (bracket, name) -> StringUtils.isBlank(name) ? bracket == null : name.equalsIgnoreCase(bracket.getName());

	@Autowired
	PokemonCardMapper pokemonCardMapper;

	@Autowired
	IBulbapediaParser bulbapediaParser;

	@ParameterizedTest
	@MethodSource("provideValidCards")
	void mapBulbapediaPokemonCard(BulbapediaPokemonCard card) {
		PokemonCardDTO dto = pokemonCardMapper.mapToDto(card, Localization.JAPAN);

		assertThat(dto).usingRecursiveComparison()
				.ignoringFields("total", "featureIds", "localization", "inDataBase", "association", "card", "translationId", "id", "available", "translations", "setIds", "parentId", "brackets", "bulbapediaContexts", "distribution", "idPrim", "fullArt", "extractionStatus", "artist", "level", "tags")
				.withEqualsForFields(BRACKET_PREDICATE, "bracket")
				.isEqualTo(card);
		assertThat(dto.getTranslations().values())
				.isNotEmpty();
	}

	@Test
	void mapBulbapediaPokemonCard_with_no_number() {
		var card = new BulbapediaPokemonCard();
		card.setName("Bulbasaur");
		card.setType("Grass");
		card.setNumber("");
		card.setRarity("Common");

		PokemonCardDTO dto = pokemonCardMapper.mapToDto(card, Localization.JAPAN);

		assertThat(dto.getTranslations().get(Localization.JAPAN).getNumber()).isEqualTo(PokemonCardHelper.NO_NUMBER);
	}

//	@ParameterizedTest
//	@MethodSource("provideValidCardDTOs")
	void update(PokemonCardDTO dto) {
		var card = new PokemonCard();
		pokemonCardMapper.update(card, dto);

		assertThat(card).usingRecursiveComparison()
				.isEqualTo(dto);
	}

	private Stream<Arguments> provideValidCards() {
	    return getCardList().stream().map(Arguments::of).limit(5);
	}

	private Stream<Arguments> provideValidCardDTOs() {
		return Stream.of(
				Arguments.of(PokemonCardTestProvider.venusaur()),
				Arguments.of(PokemonCardTestProvider.venusaurEX()),
				Arguments.of(PokemonCardTestProvider.applin()));
	}

	private List<BulbapediaPokemonCard> getCardList() {
		return bulbapediaParser.parseExtensionPage("/wiki/XY_(TCG)", "Collection X", "", "001/060", false);
	}

	@Test
	void mapBulbapediaPokemonCards() {
		List<BulbapediaPokemonCard> cards = getCardList();
		List<PokemonCardDTO> dtos = cards.stream()
				.map(card -> pokemonCardMapper.mapToDto(card, Localization.JAPAN))
				.toList();

		assertThat(dtos).isNotEmpty().hasSize(cards.size());
	}
}
