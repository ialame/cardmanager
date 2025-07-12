package com.pcagrade.retriever.card.pokemon.bracket;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.pokemon.bracket.translation.BracketTranslationDTO;
import com.pcagrade.mason.localization.ILocalized;
import com.pcagrade.mason.localization.Localization;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;

public class BracketDTO implements ILocalized {

	public static final Comparator<BracketDTO> CHANGES_COMPARATOR = Comparator.comparing(BracketDTO::getLocalization)
			.thenComparing(BracketDTO::getName, ObjectUtils::compare);

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private Ulid id;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private Localization localization;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private Map<Localization, BracketTranslationDTO> translations = new EnumMap<>(Localization.class);

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private boolean bold;

	public Ulid getId() {
		return id;
	}

	public void setId(Ulid id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Localization getLocalization() {
		return localization;
	}

	@Override
	public void setLocalization(Localization localization) {
		this.localization = localization;
	}

	@Override
	public String toString() {
		return StringUtils.isNotBlank(name) ? "[" + name + "]" : "";
	}

	public boolean isBold() {
		return bold;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public Map<Localization, BracketTranslationDTO> getTranslations() {
		return translations;
	}

	public void setTranslations(Map<Localization, BracketTranslationDTO> translations) {
		this.translations = translations;
	}
}
