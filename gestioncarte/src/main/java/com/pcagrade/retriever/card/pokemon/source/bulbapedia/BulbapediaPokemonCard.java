package com.pcagrade.retriever.card.pokemon.source.bulbapedia;

import com.pcagrade.retriever.card.pokemon.PokemonCardHelper;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction.BulbapediaExtractionFeature;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class BulbapediaPokemonCard {

	private String name;
	private String number;
	private String link;
	private String type;
	private String type2;
	private String rarity;
	private final List<String> brackets;
	private final List<BulbapediaExtractionFeature> features;
	private final List<String> promos;
	private boolean alternate;
	private int lineCount;

	public BulbapediaPokemonCard() {
		features = new ArrayList<>();
		promos = new ArrayList<>();
		brackets = new ArrayList<>();
		lineCount = 1;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRarity() {
		return rarity;
	}

	public void setRarity(String rarity) {
		this.rarity = rarity;
	}

	public List<String> getBrackets() {
		return brackets;
	}

	public void addBracket(String bracket) {
		this.brackets.add(bracket);
	}

	public void addFeature(BulbapediaExtractionFeature feature) {
		features.add(feature);
	}

	public List<BulbapediaExtractionFeature> getFeatures() {
		return features;
	}

	public List<String> getPromos() {
		return promos;
	}

	public void addPromo(String promo) {
		this.promos.add(promo);
	}


	public void setAlternate(boolean alternate) {
		this.alternate = alternate;
	}

	public boolean isAlternate() {
		return alternate || PokemonCardHelper.isAlternate(number);
	}


	public int getLineCount() {
		return lineCount;
	}

	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}

	public boolean filter(String filter) {
		return StringUtils.isBlank(filter) || StringUtils.containsIgnoreCase(name, filter) || StringUtils.equalsIgnoreCase(number, filter)
				|| brackets.stream().anyMatch(b -> StringUtils.containsIgnoreCase(b, filter))
				|| promos.stream().anyMatch(p -> StringUtils.containsIgnoreCase(p, filter))
				|| features.stream().anyMatch(f -> StringUtils.containsIgnoreCase(f.getImgHref(), filter) || StringUtils.containsIgnoreCase(f.getTitle(), filter));
	}

	@Override
	public String toString() {
		return name + (CollectionUtils.isEmpty(brackets) ? " " : " [" + String.join("; ", brackets) + "] ") + link + " " + number + " " + type + " " + rarity;
	}

	public String getType2() {
		return type2;
	}

	public void setType2(String type2) {
		this.type2 = type2;
	}
}
