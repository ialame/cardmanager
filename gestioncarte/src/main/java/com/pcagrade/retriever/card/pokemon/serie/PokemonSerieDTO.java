package com.pcagrade.retriever.card.pokemon.serie;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.pokemon.serie.translation.PokemonSerieTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

public class PokemonSerieDTO {

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private Ulid id;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private Map<Localization, PokemonSerieTranslationDTO> translations;

	public Ulid getId() {
		return id;
	}

	public void setId(Ulid id) {
		this.id = id;
	}

	public Map<Localization, PokemonSerieTranslationDTO> getTranslations() {
		return translations;
	}

	public void setTranslations(Map<Localization, PokemonSerieTranslationDTO> translations) {
		this.translations = translations;
	}

}
