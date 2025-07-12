package com.pcagrade.retriever.card.pokemon.set.translation;

import com.pcagrade.mason.localization.ILocalized;
import com.pcagrade.mason.localization.Localization;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public class PokemonSetTranslationDTO implements ILocalized {

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private Localization localization;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private boolean available = true;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private String originalName = "";
	private LocalDate releaseDate;

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

	public LocalDate getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(LocalDate releaseDate) {
		this.releaseDate = releaseDate;
	}
}
