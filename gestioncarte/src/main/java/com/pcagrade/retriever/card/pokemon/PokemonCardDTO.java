package com.pcagrade.retriever.card.pokemon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.extraction.status.CardExtractionStatusDTO;
import com.pcagrade.retriever.card.pokemon.bracket.BracketDTO;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationDTO;
import com.pcagrade.retriever.card.promo.PromoCardDTO;
import com.pcagrade.retriever.card.tag.CardTagDTO;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.localization.LocalizationUtils;
import com.pcagrade.mason.ulid.UlidHelper;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@JsonSerialize(as = PokemonCardDTO.class)
public class PokemonCardDTO {

	public static final Comparator<PokemonCardDTO> CHANGES_COMPARATOR = Comparator.comparing(PokemonCardDTO::getType, PCAUtils::compareTrimmed)
			.thenComparing(PokemonCardDTO::getType2, PCAUtils::compareTrimmed)
			.thenComparing(PokemonCardDTO::getArtist, PCAUtils::compareTrimmed)
			.thenComparingInt(PokemonCardDTO::getLevel)
			.thenComparing((c1, c2) -> LocalizationUtils.translationComparator(PokemonCardTranslationDTO.CHANGES_COMPARATOR).compare(c1.getTranslations(), c2.getTranslations()))
			.thenComparing(PokemonCardDTO::getBrackets, PCAUtils.collectionComparator(BracketDTO.CHANGES_COMPARATOR))
			.thenComparing(PokemonCardDTO::getTags, PCAUtils.collectionComparator(CardTagDTO.CHANGES_COMPARATOR))
			.thenComparing(PokemonCardDTO::getFeatureIds, PCAUtils.collectionComparator(UlidHelper::compare))
			.thenComparing(PokemonCardDTO::getSetIds, PCAUtils.collectionComparator(UlidHelper::compare))
			.thenComparing(PokemonCardDTO::getPromos, PCAUtils.collectionComparator(PromoCardDTO.CHANGES_COMPARATOR));

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private Ulid id;
	private String link;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private String type = "";
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private String type2 = "";
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private List<BracketDTO> brackets;
	private Ulid parentId;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private List<Ulid> featureIds;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private Map<Localization, PokemonCardTranslationDTO> translations;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private List<Ulid> setIds;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private List<PromoCardDTO> promos;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private List<CardTagDTO> tags;
	private boolean distribution = false;
	private boolean alternate;
	private String idPrim;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private boolean fullArt = false;
	private CardExtractionStatusDTO extractionStatus;
	private String artist;
	private int level;

	public PokemonCardDTO() {
		brackets = new ArrayList<>();
		featureIds = new ArrayList<>();
		translations = new EnumMap<>(Localization.class);
		setIds = new ArrayList<>();
		promos = new ArrayList<>();
		tags = new ArrayList<>();
	}

	public Ulid getId() {
		return id;
	}

	public void setId(Ulid id) {
		this.id = id;
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

	public List<BracketDTO> getBrackets() {
		return brackets;
	}

	public void setBrackets(List<BracketDTO> brackets) {
		this.brackets = brackets;
	}

	public List<Ulid> getFeatureIds() {
		return featureIds;
	}

	public void setFeatureIds(List<Ulid> featuresIds) {
		this.featureIds = featuresIds;
	}

	@JsonIgnore
	public String getCard() {
		PokemonCardTranslationDTO us = this.getTranslations().get(Localization.USA);

		if (us != null) {
			return us.getName();
		}
		PokemonCardTranslationDTO jap = this.getTranslations().get(Localization.JAPAN);

		if (jap != null) {
			return jap.getName();
		}
		return "";
	}

	public Map<Localization, PokemonCardTranslationDTO> getTranslations() {
		return translations;
	}

	public void setTranslations(Map<Localization, PokemonCardTranslationDTO> translations) {
		this.translations = translations;
	}

	public List<Ulid> getSetIds() {
		return setIds;
	}

	public void setSetIds(List<Ulid> setIds) {
		this.setIds = setIds;
	}

	public List<PromoCardDTO> getPromos() {
		return promos;
	}

	public void setPromos(List<PromoCardDTO> promos) {
		this.promos = promos;
	}

	public Ulid getParentId() {
		return parentId;
	}

	public void setParentId(Ulid parentId) {
		this.parentId = parentId;
	}

	public boolean filter(String filter) {
		return StringUtils.isBlank(filter)
				|| translations.values().stream().anyMatch(t -> StringUtils.containsIgnoreCase(t.getName(), filter) || StringUtils.containsIgnoreCase(t.getLabelName(), filter) || StringUtils.equalsIgnoreCase(t.getNumber(), filter))
				|| brackets.stream().anyMatch(b -> StringUtils.containsIgnoreCase(b.getName(), filter))
				|| promos.stream().anyMatch(p -> StringUtils.containsIgnoreCase(p.getName(), filter));
	}

	@JsonIgnore
	public String getDisplay() {
		return getCard() + " (" + id + ")";
	}

	@Override
	public String toString() {
		return getDisplay();
	}

	public boolean isDistribution() {
		return distribution;
	}

	public void setDistribution(boolean distribution) {
		this.distribution = distribution;
	}

	public boolean isAlternate() {
		return alternate;
	}

	public void setAlternate(boolean alternate) {
		this.alternate = alternate;
	}

	public String getIdPrim() {
		return idPrim;
	}

	public void setIdPrim(String idPrim) {
		this.idPrim = idPrim;
	}

	public boolean isFullArt() {
		return fullArt;
	}

	public void setFullArt(boolean fullArt) {
		this.fullArt = fullArt;
	}

	public CardExtractionStatusDTO getExtractionStatus() {
		return extractionStatus;
	}

	public void setExtractionStatus(CardExtractionStatusDTO extractionStatus) {
		this.extractionStatus = extractionStatus;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public List<CardTagDTO> getTags() {
		return tags;
	}

	public void setTags(List<CardTagDTO> tags) {
		this.tags = tags;
	}

	public String getType2() {
		return type2;
	}

	public void setType2(String type2) {
		this.type2 = type2;
	}
}
