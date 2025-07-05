package com.pcagrade.retriever.card.pokemon.set;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.pokemon.set.extraction.status.PokemonSetExtractionStatusDTO;
import com.pcagrade.retriever.card.pokemon.set.translation.PokemonSetTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.EnumMap;
import java.util.Map;

public class PokemonSetDTO {

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private Ulid id;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private Map<Localization, PokemonSetTranslationDTO> translations = new EnumMap<>(Localization.class);
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private String totalNumber;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private String printedTotal;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private Ulid serieId;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private boolean promo;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private String shortName;
	private Ulid parentId;
	private PokemonSetExtractionStatusDTO extractionStatus;

	public Ulid getId() {
		return id;
	}

	public void setId(Ulid id) {
		this.id = id;
	}

	public Map<Localization, PokemonSetTranslationDTO> getTranslations() {
		return translations;
	}

	public void setTranslations(Map<Localization, PokemonSetTranslationDTO> translations) {
		this.translations = translations;
	}

	public String getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(String totalNumber) {
		this.totalNumber = totalNumber;
	}

	public Ulid getSerieId() {
		return serieId;
	}

	public void setSerieId(Ulid serieId) {
		this.serieId = serieId;
	}

	public boolean isPromo() {
		return promo;
	}

	public void setPromo(boolean promo) {
		this.promo = promo;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Ulid getParentId() {
		return parentId;
	}

	public void setParentId(Ulid parentId) {
		this.parentId = parentId;
	}

	public PokemonSetExtractionStatusDTO getExtractionStatus() {
		return extractionStatus;
	}

	public void setExtractionStatus(PokemonSetExtractionStatusDTO extractionStatus) {
		this.extractionStatus = extractionStatus;
	}

	public String getPrintedTotal() {
		return printedTotal;
	}
	public void setPrintedTotal(String printedTotal) {
		this.printedTotal = printedTotal;
	}
}
