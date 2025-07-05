package com.pcagrade.retriever.card.pokemon.translation;

import com.pcagrade.mason.localization.Localization;

public class PokemonCardTranslationTestProvider {

	public static PokemonCardTranslationDTO us() {
		var us = new PokemonCardTranslationDTO();

		us.setLocalization(Localization.USA);
		return us;
	}

	public static PokemonCardTranslationDTO jp() {
		var jp = new PokemonCardTranslationDTO();

		jp.setLocalization(Localization.JAPAN);
		return jp;
	}

	public static PokemonCardTranslationDTO venusaurDTO() {
		var us = us();

		us.setName("Venusaur");
		us.setLabelName("Venusaur");
		us.setNumber("2/146");
		return us;
	}

	public static PokemonCardTranslationDTO frosmoth() {
		var us = us();

		us.setName("Frosmoth");
		us.setLabelName("Frosmoth");
		us.setNumber("SWSH007");
		return us;
	}

	public static PokemonCardTranslationDTO skiddo() {
		var us = us();

		us.setName("Skiddo");
		us.setLabelName("Skiddo");
		us.setNumber("XY11");
		return us;
	}

	public static PokemonCardTranslationDTO applinDTO() {
		var us = us();

		us.setName("Applin");
		us.setLabelName("Applin");
		us.setNumber("020/192");
		return us;
	}

	public static PokemonCardTranslationDTO florizarre() {
		var translation = new PokemonCardTranslationDTO();

		translation.setLocalization(Localization.FRANCE);
		translation.setName("Florizarre");
		translation.setLabelName("Florizarre");
		translation.setNumber("2/146");
		return translation;
	}

    public static PokemonCardTranslationDTO grookeyDTO() {
		var us = us();

		us.setName("Grookey");
		us.setLabelName("Grookey");
		us.setNumber("SWSH001");
		return us;
    }

	public static PokemonCardTranslationDTO ProfessorsResearchDTO() {
		var us = us();

		us.setName("Professor's Research");
		us.setLabelName("Professor's Research");
		us.setNumber("078/078");
		return us;
	}

	public static PokemonCardTranslationDTO bulbasaurDTO() {
		var jp = jp();

		jp.setName("Bulbasaur");
		jp.setLabelName("Bulbasaur");
		jp.setNumber("001/071");
		return jp;
	}
}
