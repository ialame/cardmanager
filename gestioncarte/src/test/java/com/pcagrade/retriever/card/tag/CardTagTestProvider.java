package com.pcagrade.retriever.card.tag;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import com.pcagrade.retriever.card.tag.translation.CardTagTranslation;
import com.pcagrade.retriever.card.tag.type.CardTagType;
import com.pcagrade.mason.localization.Localization;

import java.util.List;
import java.util.Map;

public class CardTagTestProvider {

	public static Ulid NORMAL_FORM_ID = UlidCreator.getUlid();
	public static Ulid ATTACK_FORM_ID = UlidCreator.getUlid();
	public static Ulid GALAR_ID = UlidCreator.getUlid();

	public static List<CardTag> LIST = List.of(normalForme(), attackForme(), galar());

	public static CardTag normalForme() {
		var tag = new CardTag();
		var us = new CardTagTranslation();

		tag.setId(NORMAL_FORM_ID);
		tag.setType(CardTagType.FORME);
		us.setName("Normal Forme");
		us.setId(UlidCreator.getUlid());
		tag.setTranslations(Map.of(Localization.USA, us));
		return tag;
	}
	public static CardTag attackForme() {
		var tag = new CardTag();
		var us = new CardTagTranslation();

		tag.setId(ATTACK_FORM_ID);
		tag.setType(CardTagType.FORME);
		us.setName("Attack Forme");
		us.setId(UlidCreator.getUlid());
		tag.setTranslations(Map.of(Localization.USA, us));
		return tag;
	}

	public static CardTag galar() {
		var tag = new CardTag();
		var us = new CardTagTranslation();

		tag.setId(GALAR_ID);
		tag.setType(CardTagType.REGIONAL_FORM);
		us.setName("Galar");
		us.setId(UlidCreator.getUlid());
		tag.setTranslations(Map.of(Localization.USA, us));
		return tag;
	}

}
