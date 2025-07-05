package com.pcagrade.retriever.card.pokemon.translation;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.pokemon.PokemonCardHelper;
import com.pcagrade.mason.localization.ILocalized;
import com.pcagrade.mason.localization.Localization;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

@JsonSerialize(as = PokemonCardTranslationDTO.class)
public class PokemonCardTranslationDTO implements ILocalized {

	public static final Comparator<PokemonCardTranslationDTO> CHANGES_COMPARATOR = Comparator.comparing(PokemonCardTranslationDTO::getLocalization)
			.thenComparing(PokemonCardTranslationDTO::getNumber, PCAUtils::compareTrimmedIgnoreCase)
			.thenComparing(PokemonCardTranslationDTO::getName, PCAUtils::compareTrimmed)
			.thenComparing(PokemonCardTranslationDTO::getLabelName, PCAUtils::compareTrimmed)
			.thenComparing(PokemonCardTranslationDTO::getOriginalName, PCAUtils::compareTrimmed)
			.thenComparing(PokemonCardTranslationDTO::getRarity, PCAUtils::compareTrimmedIgnoreCase)
			.thenComparing(PokemonCardTranslationDTO::getTrainer, PCAUtils::compareTrimmed);

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private Localization localization;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private String labelName;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private String number;
	private String originalName = "";
	private String rarity;
	private String trainer = "";

	private boolean available = true;

	@Override
	public Localization getLocalization() {
		return localization;
	}

	@Override
	public void setLocalization(Localization localization) {
		this.localization = localization;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public String getNumber() {
		return StringUtils.defaultIfBlank(number, PokemonCardHelper.NO_NUMBER);
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public String getRarity() {
		return rarity;
	}

	public void setRarity(String rarity) {
		this.rarity = rarity;
	}

	public PokemonCardTranslationDTO copy() {
		var target = new PokemonCardTranslationDTO();

		target.setLocalization(localization);
		target.setName(name);
		target.setLabelName(labelName);
		target.setOriginalName(originalName);
		target.setNumber(number);
		target.setRarity(rarity);
		target.setTrainer(trainer);
		return target;
	}

	public String getTrainer() {
		return trainer;
	}

	public void setTrainer(String trainer) {
		this.trainer = trainer;
	}
}
