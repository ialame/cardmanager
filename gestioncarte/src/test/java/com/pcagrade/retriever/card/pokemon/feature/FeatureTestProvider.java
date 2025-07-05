package com.pcagrade.retriever.card.pokemon.feature;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.TestUlidProvider;
import com.pcagrade.retriever.card.pokemon.feature.translation.FeatureTranslation;
import com.pcagrade.retriever.card.pokemon.feature.translation.FeatureTranslationDTO;
import com.pcagrade.mason.localization.Localization;

public class FeatureTestProvider {

	public static final Ulid MEGA_ID = Ulid.from("01G4GFKS1X47GNF95CT50H17KF");
	public static final Ulid EX_ID = Ulid.from("01G4GFKS1X47GNF95CT50H17JQ");
	public static final Ulid EX_NON_TERA_ID = Ulid.from("01GQADPAF1HMH2TM2ASR0GJ2XE");

	public static Feature mega() {
		var feature = new Feature();
		var us = new FeatureTranslation();
		var jp = new FeatureTranslation();

		feature.setId(MEGA_ID);
		feature.setPcaName("Mega");
		feature.setName("Mega");
		us.setId(TestUlidProvider.ID_1);
		us.setName("Mega");
		us.setLocalization(Localization.USA);
		us.setZebraName("Mega test");
		feature.setTranslation(Localization.USA, us);
		jp.setId(TestUlidProvider.ID_2);
		jp.setName("Mega");
		jp.setLocalization(Localization.JAPAN);
		jp.setZebraName("Mega test");
		feature.setTranslation(Localization.JAPAN, jp);
		return feature;
	}

	public static Feature ex() {
		var feature = new Feature();
		var us = new FeatureTranslation();
		var jp = new FeatureTranslation();
		var fr = new FeatureTranslation();

		feature.setId(EX_ID);
		feature.setPcaName("EX");

		us.setId(TestUlidProvider.ID_3);
		us.setName("EX");
		us.setLocalization(Localization.USA);
		us.setZebraName("EX test");
		feature.setTranslation(Localization.USA, us);
		jp.setId(TestUlidProvider.ID_3);
		jp.setName("EX");
		jp.setLocalization(Localization.JAPAN);
		jp.setZebraName("EX test");
		feature.setTranslation(Localization.JAPAN, jp);
		fr.setId(TestUlidProvider.ID_3);
		fr.setName("EX");
		fr.setLocalization(Localization.FRANCE);
		fr.setZebraName("EX test");
		feature.setTranslation(Localization.FRANCE, fr);
		return feature;
	}

	public static Feature exNonTera() {
		var feature = new Feature();
		var us = new FeatureTranslation();
		var jp = new FeatureTranslation();
		var fr = new FeatureTranslation();

		feature.setId(EX_NON_TERA_ID);
		feature.setPcaName("ex non Terastal bleu fonce");

		us.setId(TestUlidProvider.ID_4);
		us.setName("ex");
		us.setLocalization(Localization.USA);
		us.setZebraName("ex");
		feature.setTranslation(Localization.USA, us);
		jp.setId(TestUlidProvider.ID_4);
		jp.setName("ex");
		jp.setLocalization(Localization.JAPAN);
		jp.setZebraName("ex");
		feature.setTranslation(Localization.JAPAN, jp);
		fr.setId(TestUlidProvider.ID_4);
		fr.setName("ex");
		fr.setLocalization(Localization.FRANCE);
		fr.setZebraName("ex");
		feature.setTranslation(Localization.FRANCE, fr);
		return feature;
	}

	public static FeatureDTO exDTO() {
		var feature = new FeatureDTO();
		var translation = new FeatureTranslationDTO();

		feature.setId(EX_ID);
		feature.setPcaName("EX");

		translation.setName("EX");
		translation.setLocalization(Localization.USA);
		translation.setZebraName("EX test");
		feature.getTranslations().put(Localization.USA, translation);
		return feature;
	}

}
