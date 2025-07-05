package com.pcagrade.retriever.card.pokemon.feature;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.pokemon.feature.translation.FeatureTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;

public class FeatureDTO {

	public static final Comparator<FeatureDTO> CHANGES_COMPARATOR = Comparator.comparing(FeatureDTO::getId);

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private Ulid id;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private String pcaName;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private boolean visible;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private int order;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private Map<Localization, FeatureTranslationDTO> translations = new EnumMap<>(Localization.class);
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private boolean pcaFeature;

	public Ulid getId() {
		return id;
	}

	public void setId(Ulid id) {
		this.id = id;
	}

	public String getPcaName() {
		return pcaName;
	}

	public void setPcaName(String pcaName) {
		this.pcaName = pcaName;
	}

	public Map<Localization, FeatureTranslationDTO> getTranslations() {
		return translations;
	}

	public void setTranslations(Map<Localization, FeatureTranslationDTO> translations) {
		this.translations = translations;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public boolean isPcaFeature() {
		return pcaFeature;
	}

	public void setPcaFeature(boolean pcaFeature) {
		this.pcaFeature = pcaFeature;
	}
}
