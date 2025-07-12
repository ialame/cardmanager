package com.pcagrade.retriever.card.pokemon.source.bulbapedia.parser;

import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.pokemon.PokemonCardHelper;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.BulbapediaPokemonCard;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.BulbapediaPokemonCardPage2;
import com.pcagrade.retriever.card.pokemon.tag.TeraType;
import jakarta.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(BulbapediaParserTestConfig.class)
class BulbapediaParserShould {

	@Autowired
	IBulbapediaParser bulbapediaParser;

	@ParameterizedTest
	@MethodSource("provideValidTables")
	void parseExtension_with_validTable(String url, String table, String firstNumber, boolean promo, int count) {
		var list = bulbapediaParser.parseExtensionPage(url, table, "", firstNumber, promo);

		assertThat(list).isNotEmpty().hasSize(count)
				.allMatch(card -> StringUtils.isNotBlank(card.getName()), "name")
				.allMatch(card -> StringUtils.isNotBlank(card.getLink()), "link")
				.allMatch(card -> StringUtils.isNotBlank(card.getType()), "type")
				.allMatch(card -> StringUtils.isNotBlank(card.getNumber()), "number")
				.allMatch(card -> !promo || CollectionUtils.isNotEmpty(card.getPromos()), "promos")
				.allSatisfy(card -> {
					var name = card.getName();

					if (!StringUtils.containsIgnoreCase(name, "Champion")) {
						assertThat(card.getBrackets()).noneSatisfy(b -> assertThat(name).containsIgnoringCase(b));
					}
				});
		assertThat(list.isEmpty() ? null :list.get(0).getNumber()).isEqualTo(firstNumber);
	}

	@Test
	void parseExtension_return_differentLists_with_similarTables() {
		var list1 = bulbapediaParser.parseExtensionPage("https://bulbapedia.bulbagarden.net/wiki/Zeraora_VSTAR_%26_VMAX_High-Class_Deck_(TCG)", "Zeraora VSTAR & VMAX High-Class Deck", "", "001/020", false);
		var list2 = bulbapediaParser.parseExtensionPage("https://bulbapedia.bulbagarden.net/wiki/Deoxys_VSTAR_%26_VMAX_High-Class_Deck_(TCG)",  "Deoxys VSTAR & VMAX High-Class Deck", "", "001/020", false);

		assertThat(list1).isNotEmpty().hasSize(21);
		assertThat(list2).isNotEmpty().hasSize(21);

		assertThat(list1.isEmpty() ? null :list1.get(0).getName()).isEqualTo("Lumineon {0}");
		assertThat(list2.isEmpty() ? null :list2.get(0).getName()).isEqualTo("Staryu");
		assertThat(list1.get(20).getName()).isEqualTo("Lightning Energy");
		assertThat(list2.get(20).getName()).isEqualTo("Psychic Energy");
		for (int i = 0; i < 21; i++) {
			assertThat(list1.get(i)).usingRecursiveComparison().isNotEqualTo(list2.get(i));
		}
	}
	
	@Test
	void parseExtension_check_name_and_labelName() {
		var list = bulbapediaParser.parseExtensionPage("/wiki/XY_(TCG)", "Collection X", "", "001/060", false);

		assertThat(list).isNotEmpty();

		var venusaur = list.isEmpty() ? null :list.get(0);

		assertThat(venusaur) .hasFieldOrPropertyWithValue("name", "Venusaur {0}");
		assertThat(venusaur.getFeatures()).isNotEmpty();
		assertThat(venusaur.getFeatures().isEmpty() ? null :venusaur.getFeatures().get(0)).hasFieldOrPropertyWithValue("title", "EX");
	}


	@Test
	void parseExtension_with_brackets() {
		var list = bulbapediaParser.parseExtensionPage("/wiki/SM-P_Promotional_cards_(TCG)", "SM-P Promotional cards", "", "001/SM-P", true).stream()
				.filter(card -> StringUtils.equals(card.getNumber(), "SM-P") && StringUtils.equalsIgnoreCase(card.getName(), "Rare Candy"))
				.toList();

		assertThat(list)
				.hasSize(3)
				.allSatisfy(card -> {
					assertThat(card.getName()).isEqualTo("Rare Candy");
					assertThat(card.getNumber()).isEqualTo("SM-P");
					assertThat(card.getBrackets()).isNotEmpty();
				});
	}

	@Test
	void parseExtension_with_bold_brackets() {
		var list = bulbapediaParser.parseExtensionPage("/wiki/Secret_Wonders_(TCG)", "Secret Wonders", "", "1/132", false).stream()
				.filter(card -> StringUtils.equals(card.getNumber(), "8/132"))
				.toList();

		assertThat(list)
				.hasSize(1)
				.allSatisfy(card -> {
					assertThat(card.getName()).isEqualTo("Gastrodon");
					assertThat(card.getNumber()).isEqualTo("8/132");
					assertThat(card.getBrackets()).isNotEmpty()
							.hasSize(1)
							.allSatisfy(bracket -> assertThat(bracket).isEqualTo("East Sea"));
				});
	}

	@Test
	void parseExtension_with_energyWithImage() {
		var list = bulbapediaParser.parseExtensionPage("/wiki/S-P_Promotional_cards_(TCG)", "S-P Promotional cards", "", "001/S-P", true).stream()
				.filter(card -> StringUtils.equals(card.getNumber(), "049/S-P"))
				.toList();

		assertThat(list)
				.hasSize(1)
				.allSatisfy(card -> {
					assertThat(card.getName()).isEqualTo("Speed Lightning Energy");
					assertThat(card.getBrackets()).isEmpty();
				});
	}

	@Test
	void parseExtension_with_h3() {
		var list = bulbapediaParser.parseExtensionPage("/wiki/Beginning_Set_(TCG)", "Snivy Half Deck", "Beginning Set", "001/037", false);

		assertThat(list).hasSize(14);

		var snivy = list.get(0);

		assertThat(snivy)
				.hasFieldOrPropertyWithValue("name", "Snivy");
		assertThat(snivy.getFeatures()).isEmpty();
	}

	@Test
	void parse_scarlet_ex() {
		var list = bulbapediaParser.parseExtensionPage("/wiki/Scarlet_%26_Violet_(TCG)", "Scarlet ex", "", "001/078", false);

		assertThat(list).hasSize(108);

		var gyarados = list.get(13);

		assertThat(gyarados.getName()).isEqualTo("Gyarados {0}");
		assertThat(gyarados.getFeatures()).hasSize(1).allSatisfy(f -> {
			assertThat(f.getTitle()).isEqualTo("Tera ex");
			assertThat(f.getImgHref()).isEqualTo("Tera ex");
		});
	}

	@Test
	void parse_sp_promo() {
		var list = bulbapediaParser.parseExtensionPage("/wiki/S-P_Promotional_cards_(TCG)", "S-P Promotional cards", "", "001/S-P", false);
		
		assertThat(list).isNotEmpty();

		var jirachiGX = list.stream()
				.filter(c -> StringUtils.equals(c.getNumber(), "196/S-P"))
				.findFirst();

		assertThat(jirachiGX).hasValueSatisfying(c -> {
			assertThat(c.getNumber()).isEqualTo("196/S-P");
			assertThat(c.getName()).isEqualTo("Jirachi {0}");
			assertThat(c.getFeatures()).hasSize(1).allSatisfy(f -> assertThat(f.getTitle()).isEqualTo("GX"));
		});
	}

	@Test
	void parse_double_type() {
		var list = bulbapediaParser.parseExtensionPage("/wiki/EX_Team_Magma_vs_Team_Aqua_(TCG)", "EX Team Magma vs Team Aqua", "", "1/95", false);

		assertThat(list).hasSize(97);
		assertThat(list.isEmpty() ? null :list.get(0)).satisfies(c -> {
			assertThat(c.getName()).isEqualTo("Team Aqua's Cacturne");
			assertThat(c.getType()).isEqualTo("Grass");
			assertThat(c.getType2()).isEqualTo("Darkness");
		});
	}

	@Test
	void parse_unnumbered_1997() {
		var list = bulbapediaParser.parseExtensionPage("/wiki/Unnumbered_Promotional_cards_(TCG)/1996-2005", "1997 releases", "", "—", true);

		assertThat(list).hasSize(28);
		assertThat(list.isEmpty() ? null :list.get(0)).satisfies(c -> {
			assertThat(c.getName()).isEqualTo("Mew");
			assertThat(c.getType()).isEqualTo("Psychic");
			assertThat(c.getBrackets()).isNotEmpty().hasSize(1).allSatisfy(b -> assertThat(b).isEqualTo("Glossy"));
		});
	}

	@ParameterizedTest
	@MethodSource("provideValidCardsInPage2")
	void isInPage2_with_validCard(BulbapediaPokemonCard card, Localization localization, String setName, boolean unnumbered) {
		var inPage2 = bulbapediaParser.isInPage2(card.getLink(), card.getNumber(), PokemonCardHelper.otherLocalization(localization), setName, unnumbered);

		assertThat(inPage2).isTrue();
	}

	@Test
	void is_not_InPage2_with_invalidUrl() {
		var inPage2 = bulbapediaParser.isInPage2("", "001/060", Localization.USA, "Collection X", false);

		assertThat(inPage2).isFalse();
	}

	@ParameterizedTest
	@MethodSource("provideValidCards")
	void findAssociation_with_validCard(BulbapediaPokemonCard card, Localization localization, String setName, boolean unnumbered) {
		List<BulbapediaPokemonCardPage2> association = bulbapediaParser.findAssociatedCards(card, localization, setName, unnumbered);

		assertThat(association).isNotEmpty();
	}

	@ParameterizedTest
	@MethodSource("provideValidPureCards")
	void findAssociation_with_validPureCard(BulbapediaPokemonCard card, Localization localization, String setName, boolean unnumbered) {
		List<BulbapediaPokemonCardPage2> association = bulbapediaParser.findAssociatedCards(card, localization, setName, unnumbered);

		assertThat(association).isEmpty();
	}

	@ParameterizedTest
	@MethodSource("provideValidURLsForOriginalName")
	void findOriginalName_with_validUrl(String url, String originalName) {
		var name = bulbapediaParser.findOriginalName(url);

		assertThat(name).isEqualTo(originalName);
	}

	@ParameterizedTest
	@MethodSource("provideValidURLsForForme")
	void findForm_with_validUrl(String url, String form) {
		var name = bulbapediaParser.findForme(url);

		assertThat(name).isEqualTo(form);
	}

	@Test
	void findLevel_with_validLevel() {
		var level = bulbapediaParser.findLevel("https://bulbapedia.bulbagarden.net/wiki/Blaine%27s_Vulpix_(Gym_Challenge_66)");

		assertThat(level).isEqualTo(18);
	}

	@Test
	void findLevel_without_level() {
		var level = bulbapediaParser.findLevel("https://bulbapedia.bulbagarden.net/wiki/Venusaur-EX_(XY_1)");

		assertThat(level).isZero();
	}

	@Test
	void findArtistName_with_validUrl() {
		var name = bulbapediaParser.findArtistName("https://bulbapedia.bulbagarden.net/wiki/Venusaur-EX_(XY_1)");

		assertThat(name).isEqualTo("Eske Yoshinob");
	}

	@Test
	void findDeltaSpecies_with_validUrl() {
		var deltaSpecies = bulbapediaParser.isDeltaSpecies("https://bulbapedia.bulbagarden.net/wiki/Beedrill_%CE%B4_(EX_Delta_Species_1)");

		assertThat(deltaSpecies).isTrue();
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"https://bulbapedia.bulbagarden.net/wiki/SWSH_Black_Star_Promos_(TCG)",
			"https://bulbapedia.bulbagarden.net/wiki/151_(TCG)",
			"https://bulbapedia.bulbagarden.net/wiki/Flapple_(Rebel_Clash_22)"
	})
	void findImage_with_validUrl(String url) {
		var images = bulbapediaParser.findImages(url, Localization.USA);

		assertThat(images).isNotEmpty()
				.allSatisfy(i -> {
					assertThat(i.localization()).isEqualTo(Localization.USA);
					assertThat(i.url()).isNotBlank();
				});
	}

	@Test
	void findTeraType() {
		var type = bulbapediaParser.findTeraType("https://bulbapedia.bulbagarden.net/wiki/Arcanine_ex_(Violet_ex_16)");

		assertThat(type).isEqualTo(TeraType.FIRE);
	}

	private Stream<Arguments> provideValidTables() {
	    return Stream.of(
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Sun_%26_Moon_(TCG)", "Collection Sun", "001/060", false, 73),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Sun_%26_Moon_(TCG)", "Collection Moon", "001/060", false, 73),
			  	Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Sun_%26_Moon_(TCG)", "Sun & Moon", "1/149", false, 163),
			  	Arguments.of("https://bulbapedia.bulbagarden.net/wiki/XY_(TCG)", "Collection X", "001/060", false, 64),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/SM_Black_Star_Promos_(TCG)", "SM Black Star Promos", "SM01", true, 342),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Pok%C3%A9mon_GO_(TCG)", "Pokémon GO", "001/071", false, 101),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Pok%C3%A9mon_GO_(TCG)", "Pokémon GO", "001/078", false, 88),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Unnumbered_Promotional_cards_(TCG)", "2006 releases", "—", true, 7),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Gym_Heroes_(TCG)", "Gym Heroes", "1/132", false, 132),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/SM-P_Promotional_cards_(TCG)", "SM-P Promotional cards", "001/SM-P", true, 432),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Unnumbered_Promotional_cards_(TCG)/1996-2005", "1996 releases", "—", true, 4),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Greninja_V-UNION_Special_Card_Set_(TCG)", "Greninja V-UNION Special Card Set", "001/013", false, 5),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Mewtwo_V-UNION_Special_Card_Set_(TCG)", "Mewtwo V-UNION Special Card Set", "005/013", false, 5),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Zacian_V-UNION_Special_Card_Set_(TCG)", "Zacian V-UNION Special Card Set", "009/013", false, 5),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Zeraora_VSTAR_%26_VMAX_High-Class_Deck_(TCG)", "Zeraora VSTAR & VMAX High-Class Deck", "001/020", false, 21),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Deoxys_VSTAR_%26_VMAX_High-Class_Deck_(TCG)", "Deoxys VSTAR & VMAX High-Class Deck", "001/020", false, 21),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Entry_Pack_DPt_(TCG)", "Giratina Half Deck", "001/013", false, 13),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Entry_Pack_DPt_(TCG)", "Dialga Half Deck", "001/013", false, 13),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Entry_Pack_DPt_(TCG)", "Palkia Half Deck", "001/013", false, 13),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Incandescent_Arcana_(TCG)", "Incandescent Arcana", "001/068", false, 94),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Diamond_%26_Pearl_(TCG)", "Diamond & Pearl", "1/130", false, 130),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Diamond_%26_Pearl_(TCG)", "Diamond Collection", "—", false, 117),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Diamond_%26_Pearl_(TCG)", "Pearl Collection", "—", false, 119),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Triplet_Beat_(TCG)", "Triplet Beat", "001/073", false, 103)
	    );
	}

	private Stream<Arguments> provideValidCardsInPage2() {
		return Stream.concat(provideValidCards(), Stream.of(
				getCartToSearchInPage2("https://bulbapedia.bulbagarden.net/wiki/P_Promotional_cards_(TCG)", "P Promotional cards", "001/P", Localization.USA, "018/P"),
				getCartToSearchInPage2("https://bulbapedia.bulbagarden.net/wiki/Incandescent_Arcana_(TCG)", "Incandescent Arcana", "001/068", Localization.USA, "052/068"),
				getCartToSearchInPage2("https://bulbapedia.bulbagarden.net/wiki/Entry_Pack_DPt_(TCG)", "Dialga Half Deck",  "001/013", Localization.USA, "009/013"),
				getCartToSearchInPage2("https://bulbapedia.bulbagarden.net/wiki/Master_Kit_(TCG)", "Side Deck",  "001/012", Localization.USA, "001/012")
		).flatMap(Function.identity()));
	}

	private Stream<Arguments> provideValidCards() {
	    return Stream.of(
				bulbapediaParser.parseExtensionPage("https://bulbapedia.bulbagarden.net/wiki/XY_(TCG)", "Collection X", "", "001/060", false).stream()
						.limit(5)
						.map(c -> Arguments.of(c, Localization.USA, "Collection X", false)),
				getCartToSearchInPage2("https://bulbapedia.bulbagarden.net/wiki/The_Best_of_XY_(TCG)", "THE BEST OF XY", "001/171", Localization.USA, "182/171"),
				getCartToSearchInPage2("https://bulbapedia.bulbagarden.net/wiki/SWSH_Black_Star_Promos_(TCG)", "SWSH Black Star Promos", "SWSH001", Localization.JAPAN, "SWSH063"),
				getCartToSearchInPage2("https://bulbapedia.bulbagarden.net/wiki/SM_Black_Star_Promos_(TCG)", "SM Black Star Promos", "SM01", Localization.JAPAN, "SM190"),
				getCartToSearchInPage2("https://bulbapedia.bulbagarden.net/wiki/Crown_Zenith_(TCG)", "Crown Zenith",  "001/159", Localization.JAPAN, "002/159"),
				getCartToSearchInPage2("https://bulbapedia.bulbagarden.net/wiki/Silver_Tempest_(TCG)", "Paradigm Trigger", "001/098", Localization.USA, "002/098"),
				getCartToSearchInPage2("https://bulbapedia.bulbagarden.net/wiki/Ex_Starter_Sets_(TCG)", "ex Starter Set Quaxly & Mimikyu ex", "001/023", Localization.USA, "002/023")
		).flatMap(Function.identity());
	}

	@Nonnull
	private Stream<Arguments> getCartToSearchInPage2(String url, String tableName, String firstNumber, Localization localizationToSearch, String filter) {
		return bulbapediaParser.parseExtensionPage(url, tableName, "", firstNumber, false).stream()
				.filter(c -> filter.equals(c.getNumber()))
				.map(c -> Arguments.of(c, localizationToSearch, tableName, false));
	}

	private Stream<Arguments> provideValidPureCards() {
		return Stream.of(
				bulbapediaParser.parseExtensionPage("/wiki/XY_Trainer_Kit:_Latias_%26_Latios_(TCG)", "Latias Half Deck", "", "1/30", false).stream()
						.limit(5)
						.map(c -> Arguments.of(c, Localization.JAPAN, "Latias Half Deck", false))
		).flatMap(Function.identity());
	}

	private Stream<Arguments> provideValidURLsForOriginalName() {
		return Stream.of(
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Venusaur-EX_(XY_1)", "フシギバナEX"),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Blaine%27s_Growlithe_(Gym_Heroes_35)", "カツラのガーディ"),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Pretend_Boss_Pikachu_(SM-P_Promo_191)", "ボスごっこピカチュウ ロケット団"),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Pretend_Boss_Pikachu_(SM-P_Promo_192)", "ボスごっこピカチュウ アクア団"),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Pretend_Boss_Pikachu_(SM-P_Promo_193)", "ボスごっこピカチュウ マグマ団"),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Pretend_Boss_Pikachu_(SM-P_Promo_194)", "ボスごっこピカチュウ ギンガ団"),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Pretend_Boss_Pikachu_(SM-P_Promo_195)", "ボスごっこピカチュウ プラズマ団"),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Pretend_Boss_Pikachu_(SM-P_Promo_196)", "ボスごっこピカチュウ フレア団"),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Pretend_Boss_Pikachu_(SM-P_Promo_197)", "ボスごっこピカチュウ スカル団")
		);
	}

	private Stream<Arguments> provideValidURLsForForme() {
		return Stream.of(
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Gastrodon_(Secret_Wonders_8)", "East Sea"),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Gastrodon_(Secret_Wonders_9)", "West Sea"),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Deoxys_(Legends_Awakened_1)", "Normal Forme"),
				Arguments.of("https://bulbapedia.bulbagarden.net/wiki/Deoxys_(EX_Deoxys_16)", "")
		);
	}
}
