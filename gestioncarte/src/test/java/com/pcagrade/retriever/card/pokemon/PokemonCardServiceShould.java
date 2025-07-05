package com.pcagrade.retriever.card.pokemon;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.TestUlidProvider;
import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.pokemon.bracket.BracketTestProvider;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestProvider;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslation;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationRepository;
import com.pcagrade.retriever.card.promo.PromoCardDTO;
import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(PokemonCardServiceTestConfig.class)
class PokemonCardServiceShould {

	@Autowired
	IPokemonCardService pokemonCardService;

	@Autowired
	PokemonCardRepository pokemonCardRepository;
	@Autowired
	PokemonCardTranslationRepository pokemonCardTranslationRepository;

	@ParameterizedTest
	@MethodSource("provideValidCards")
	void findCardInSet(String name, String number, Ulid setId) {
		Stream<PokemonCardDTO> found = pokemonCardService.findCardsInSet(name, number, Localization.USA, setId, false);

		assertThat(found).isNotEmpty();
	}

	@Test
	void find_venusaur_parentCard() {
		Stream<PokemonCardDTO> found = pokemonCardService.findParentCard("Venusaur", "2a/146", Localization.USA, PokemonSetTestProvider.XY_ID);

		assertThat(found).isNotEmpty()
				.allSatisfy(card -> assertThat(card.getTranslations().get(Localization.USA).getNumber()).isEqualTo("2/146"));
	}

	@Test
	void find_applin_parentCard() {
		Stream<PokemonCardDTO> found = pokemonCardService.findParentCard("Applin", "020/192", Localization.USA, PokemonSetTestProvider.REBEL_CLASH_ID);

		assertThat(found).isNotEmpty()
				.allSatisfy(card -> assertThat(card.getTranslations().get(Localization.USA).getNumber()).isEqualTo("020/192"));
	}

	@Test
	void save_venusaur() {
		Mockito.clearInvocations(pokemonCardRepository);

		var venusaur = PokemonCardTestProvider.venusaur();

		venusaur.setId(null);
		pokemonCardService.save(venusaur);

		PokemonCard card = getCardPassedToSave();

		assertThat(card.getTranslation(Localization.USA))
				.isInstanceOf(PokemonCardTranslation.class)
				.hasFieldOrPropertyWithValue("name", "Venusaur")
				.hasFieldOrPropertyWithValue("number", "2/146");
	}

	@Test
	void save_venusaur_with_remove_us() {
		Mockito.clearInvocations(pokemonCardRepository);
		Mockito.clearInvocations(pokemonCardTranslationRepository);

		var venusaur = PokemonCardTestProvider.venusaur();

		venusaur.getTranslations().remove(Localization.USA);
		pokemonCardService.save(venusaur);

		PokemonCard card = getCardPassedToSave();
		Mockito.verify(pokemonCardTranslationRepository).delete(Mockito.any(PokemonCardTranslation.class));

		assertThat(card.getTranslation(Localization.USA)).isNull();
	}

	@ParameterizedTest
	@MethodSource("provideCardsWithFA")
	void merge_With_FA(PokemonCardDTO dto, List<Ulid> ids) {
		Mockito.clearInvocations(pokemonCardRepository);

		pokemonCardService.merge(dto, ids);

		PokemonCard card = getCardPassedToSave();
		var fa = dto.isFullArt();

		assertThat(card.isFullArt()).isEqualTo(fa);
		assertThat(card.getTranslations()).allSatisfy(translation -> {
			if (fa) {
				assertThat(translation.getName()).endsWith(" FA");
				assertThat(translation.getLabelName()).endsWith(" FA");
			} else {
				assertThat(translation.getName()).doesNotEndWith(" FA");
				assertThat(translation.getLabelName()).doesNotEndWith(" FA");
			}
		});
	}

	@Test
	void save_applin() {
		Mockito.clearInvocations(pokemonCardRepository);

		var applin = PokemonCardTestProvider.applin();

		applin.setId(null);
		pokemonCardService.save(applin);

		PokemonCard card = getCardPassedToSave();

		assertThat(card)
				.hasFieldOrPropertyWithValue("fullArt", true)
				.hasFieldOrPropertyWithValue("distribution", true)
				.matches(c -> TestUlidProvider.ID_2.equals(c.getParent().getId()));
		assertThat(card.getTranslation(Localization.USA))
				.isInstanceOf(PokemonCardTranslation.class)
				.hasFieldOrPropertyWithValue("name", "Applin FA")
				.hasFieldOrPropertyWithValue("number", "020/192");
	}

	@Test
	void save_adding_promo() {
		Mockito.clearInvocations(pokemonCardRepository);
		var promo = new PromoCardDTO();

		promo.setName("Name");
		promo.setLocalization(Localization.USA);

		var applin = PokemonCardTestProvider.applin();

		applin.setId(TestUlidProvider.ID_2);
		applin.setPromos(List.of(promo));

		pokemonCardService.save(applin);

		PokemonCard card = getCardPassedToSave();

		assertThat(card.getPromoCards()).hasSize(1)
				.allSatisfy(promoCard -> {
					assertThat(promoCard.getName()).isEqualTo("Name");
					assertThat(promoCard.getLocalization()).isEqualTo(Localization.USA);
				});
	}

	@Test
	void save_promo_without_changing_it() {
		Mockito.clearInvocations(pokemonCardRepository);
		var frosmoth = PokemonCardTestProvider.frosmothStaff();

		frosmoth.setId(TestUlidProvider.ID_3);

		pokemonCardService.save(frosmoth);

		PokemonCard card = getCardPassedToSave();

		assertThat(card.getPromoCards()).hasSize(1)
				.allSatisfy(promoCard -> {
					assertThat(promoCard.getName()).isEqualTo("Sword & Shield Prerelease staff promo");
					assertThat(promoCard.getLocalization()).isEqualTo(Localization.USA);
					assertThat(promoCard.getCard()).isEqualTo(card);
					assertThat(promoCard.getEvent().getId()).isEqualTo(TestUlidProvider.ID_1);
				});
	}

	@Test
	void save_delete_promos() {
		Mockito.clearInvocations(pokemonCardRepository);
		var frosmoth = PokemonCardTestProvider.frosmothStaff();

		frosmoth.setId(TestUlidProvider.ID_3);
		frosmoth.setPromos(Collections.emptyList());

		pokemonCardService.save(frosmoth);

		PokemonCard card = getCardPassedToSave();

		assertThat(card.getPromoCards()).isEmpty();
	}

	private PokemonCard getCardPassedToSave() {
		ArgumentCaptor<PokemonCard> captor = ArgumentCaptor.forClass(PokemonCard.class);

		Mockito.verify(pokemonCardRepository, Mockito.atLeastOnce()).save(captor.capture());
		return captor.getValue();
	}

	@Test
	void getCardById_return_empty_with_nullId() {
		var found = pokemonCardService.getCardById(null);

		assertThat(found).isEmpty();
	}

	@Test
	void rebuildIdPrims() {
		Mockito.clearInvocations(pokemonCardRepository);

		pokemonCardService.rebuildIdsPrim(PokemonSetTestProvider.COLLECTION_X_ID);

		ArgumentCaptor<PokemonCard> captor = ArgumentCaptor.forClass(PokemonCard.class);

		Mockito.verify(pokemonCardRepository, Mockito.atLeastOnce()).save(captor.capture());

		assertThat(captor.getAllValues())
				.map(PokemonCard::getIdPrimJp)
				.allSatisfy(i -> assertThat(i).startsWith("110"));
	}

	@Test
	void rebuildIdPrims_For_unnumbered() {
		Mockito.clearInvocations(pokemonCardRepository);

		pokemonCardService.rebuildIdsPrim(PokemonSetTestProvider.GUREN_TOWN_GYM_ID);

		ArgumentCaptor<PokemonCard> captor = ArgumentCaptor.forClass(PokemonCard.class);

		Mockito.verify(pokemonCardRepository, Mockito.atLeastOnce()).save(captor.capture());

		assertThat(captor.getAllValues())
				.map(PokemonCard::getIdPrimJp)
				.allSatisfy(i -> assertThat(i).startsWith("400"));
	}

	@Test
	void return_frosmoth_with_staffBracket() {
		var cards = pokemonCardService.findSavedCards(PokemonCardTestProvider.frosmothStaff());

		assertThat(cards)
				.isNotEmpty()
				.allSatisfy(card -> assertThat(card.getId()).isNotNull())
				.allSatisfy(card -> assertThat(card.getBrackets()).isNotEmpty().allSatisfy(b -> assertThat(b.getId()).isEqualTo(BracketTestProvider.STAFF_ID)))
				.allSatisfy(card -> assertThat(card.getTranslations()).allSatisfy((l, t) -> {
					assertThat(t.getName()).endsWith("Frosmoth");
					assertThat(t.getLabelName()).endsWith("Frosmoth");
				}));
	}

	@Test
	void return_frosmoth_without_staffBracket() {
		var cards = pokemonCardService.findSavedCards(PokemonCardTestProvider.frosmoth());

		assertThat(cards)
				.isNotEmpty()
				.allSatisfy(card -> assertThat(card.getId()).isNotNull())
				.allSatisfy(card -> assertThat(card.getBrackets()).isEmpty())
				.allSatisfy(card -> assertThat(card.getTranslations()).allSatisfy((l, t) -> {
					assertThat(t.getName()).endsWith("Frosmoth");
					assertThat(t.getLabelName()).endsWith("Frosmoth");
				}));
	}

	@Test
	void find_venusaur_savedCard() {
		var found = pokemonCardService.findSavedCards(PokemonCardTestProvider.venusaur());

		assertThat(found).isNotEmpty().hasSize(1)
				.allSatisfy(card -> assertThat(card.getId()).isEqualTo(TestUlidProvider.ID_101))
				.allSatisfy(card -> assertThat(card.getTranslations().get(Localization.USA).getNumber()).isEqualTo("2/146"));
	}

	@Test
	void finb_skiddoXY11_savedCard() {
		var found = pokemonCardService.findSavedCards(PokemonCardTestProvider.skiddoXY11());

		assertThat(found).isNotEmpty().hasSize(1)
				.allSatisfy(card -> assertThat(card.getId()).isEqualTo(TestUlidProvider.ID_8))
				.allSatisfy(card -> assertThat(card.getTranslations().get(Localization.USA).getNumber()).isEqualTo("XY11"));
	}

	private Stream<Arguments> provideCardsWithFA() {
		var venusaur = PokemonCardTestProvider.venusaur();
		var faVenusaur = PokemonCardTestProvider.venusaur();

		venusaur.setId(null);
		faVenusaur.setId(null);
		faVenusaur.setFullArt(true);

		return Stream.of(
				Arguments.of(faVenusaur, List.of()),
				Arguments.of(faVenusaur, List.of(TestUlidProvider.ID_101)),
				Arguments.of(venusaur, List.of(TestUlidProvider.ID_102)),
				Arguments.of(venusaur, List.of(TestUlidProvider.ID_101, TestUlidProvider.ID_102)));
	}

	private Stream<Arguments> provideValidCards() {
		return Stream.of(
				Arguments.of("Venusaur", "2/146", PokemonSetTestProvider.XY_ID),
				Arguments.of("Applin", "020/192", PokemonSetTestProvider.REBEL_CLASH_ID));
	}
}
