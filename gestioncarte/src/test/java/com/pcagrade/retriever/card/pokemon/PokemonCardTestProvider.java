package com.pcagrade.retriever.card.pokemon;

import com.pcagrade.retriever.TestUlidProvider;
import com.pcagrade.retriever.card.pokemon.bracket.BracketTestProvider;
import com.pcagrade.retriever.card.pokemon.feature.FeatureTestProvider;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestProvider;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationTestProvider;
import com.pcagrade.retriever.card.promo.PromoCardDTO;
import com.pcagrade.mason.localization.Localization;

import java.util.List;

public class PokemonCardTestProvider {

	public static PokemonCardDTO venusaur() {
		var card = new PokemonCardDTO();
		card.setId(TestUlidProvider.ID_101);
		card.setSetIds(List.of(PokemonSetTestProvider.XY_ID));
		card.getTranslations().put(Localization.USA, PokemonCardTranslationTestProvider.venusaurDTO());
		return card;
	}

	public static PokemonCardDTO venusaurEX() {
		var venusaur = venusaur();

		venusaur.getFeatureIds().add(FeatureTestProvider.EX_ID);
		venusaur.getTranslations().get(Localization.USA).setNumber("1/146");
		venusaur.getTranslations().get(Localization.USA).setName("Venusaur EX");
		venusaur.getTranslations().get(Localization.USA).setLabelName("Venusaur EX");
		return venusaur;
	}

	public static PokemonCardDTO latias() {
		var latias = venusaur();

		latias.setId(TestUlidProvider.ID_5);
		latias.setSetIds(List.of(PokemonSetTestProvider.LATIAS_HALF_DECK_ID));
		latias.getTranslations().put(Localization.USA, PokemonCardTranslationTestProvider.us());
		latias.getTranslations().get(Localization.USA).setNumber("14/30");
		latias.getTranslations().get(Localization.USA).setName("Latias");
		latias.getTranslations().get(Localization.USA).setLabelName("Latias");
		return latias;
	}

	public static PokemonCardDTO latiasHDGrassEnergy() {
		var card = venusaur();

		card.setId(TestUlidProvider.ID_6);
		card.setSetIds(List.of(PokemonSetTestProvider.LATIAS_HALF_DECK_ID));
		card.getTranslations().put(Localization.USA, PokemonCardTranslationTestProvider.us());
		card.getTranslations().get(Localization.USA).setNumber("1/30");
		card.getTranslations().get(Localization.USA).setName("Grass Energy");
		card.getTranslations().get(Localization.USA).setLabelName("Grass Energy");
		return card;
	}

	public static PokemonCardDTO applin() {
		var card = new PokemonCardDTO();

		card.setId(TestUlidProvider.ID_3);
		card.setSetIds(List.of(PokemonSetTestProvider.REBEL_CLASH_ID));
		card.setDistribution(true);
		card.setParentId(TestUlidProvider.ID_2);
		card.setFullArt(true);
		card.getTranslations().put(Localization.USA, PokemonCardTranslationTestProvider.applinDTO());
		return card;
	}

    public static PokemonCardDTO grookeyDTO() {
		var card = new PokemonCardDTO();

		card.setId(TestUlidProvider.ID_4);
		card.setSetIds(List.of(PokemonSetTestProvider.SWSH_ID));
		card.setFullArt(false);
		card.getTranslations().put(Localization.USA, PokemonCardTranslationTestProvider.grookeyDTO());
		return card;
    }

	public static PokemonCardDTO frosmoth() {
		var card = new PokemonCardDTO();
		card.setId(TestUlidProvider.ID_5);
		card.setSetIds(List.of(PokemonSetTestProvider.SWSH_ID));
		card.getTranslations().put(Localization.USA, PokemonCardTranslationTestProvider.frosmoth());
		return card;
	}

	public static PokemonCardDTO frosmothStaff() {
		var card = frosmoth();
		var promo = new PromoCardDTO();

		card.setId(TestUlidProvider.ID_6);
		card.setBrackets(List.of(BracketTestProvider.staffDTO()));
		promo.setLocalization(Localization.USA);
		promo.setName("Sword & Shield Prerelease staff promo");
		promo.setEventId(TestUlidProvider.ID_1);
		card.setPromos(List.of(promo));
		return card;
	}

	public static PokemonCardDTO skiddoXY11() {
		var card = new PokemonCardDTO();
		var promo = new PromoCardDTO();

		card.setId(TestUlidProvider.ID_8);
		card.setSetIds(List.of(PokemonSetTestProvider.PROMO_XY_ID));
		card.getTranslations().put(Localization.USA, PokemonCardTranslationTestProvider.skiddo());
		card.setBrackets(List.of(BracketTestProvider.xySuperHotStartUpLogoDTO()));
		promo.setLocalization(Localization.USA);
		promo.setName("Flashfire Blisters");
		card.setPromos(List.of(promo));
		return card;
	}

	public static PokemonCardDTO ProfessorsResearch() {
		var card = new PokemonCardDTO();
		card.setId(TestUlidProvider.ID_1);
		card.setSetIds(List.of(PokemonSetTestProvider.XY_ID));
		card.getTranslations().put(Localization.USA, PokemonCardTranslationTestProvider.ProfessorsResearchDTO());
		return card;
	}

    public static PokemonCardDTO bulbasaurDTO() {
		var card = new PokemonCardDTO();
		card.setId(TestUlidProvider.ID_7);
		card.setSetIds(List.of(PokemonSetTestProvider.POKEMON_GO_JAPAN_ID));
		card.getTranslations().put(Localization.JAPAN, PokemonCardTranslationTestProvider.bulbasaurDTO());
		return card;
    }

    public static PokemonCardDTO professorResearch() {
		var card = new PokemonCardDTO();
		var jp = PokemonCardTranslationTestProvider.jp();

		jp.setName("Professor's Research");
		jp.setLabelName("Professor's Research");
		jp.setNumber("019/021");
		card.setId(TestUlidProvider.ID_1);
		card.setSetIds(List.of(PokemonSetTestProvider.LUCARIO_VSTAR_STARTER_SET_ID));
		card.getTranslations().put(Localization.JAPAN, jp);
		return card;
    }
}
