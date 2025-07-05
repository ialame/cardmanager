package com.pcagrade.retriever.card.pokemon.source.bulbapedia.expansion;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.localization.Localization;
import io.swagger.v3.oas.annotations.media.Schema;

public class ExpansionBulbapediaDTO {

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private int id;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private Ulid setId;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private Ulid serieId;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private String page2Name;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private String h3Name;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private Localization localization = Localization.USA;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private String firstNumber;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private String tableName;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private String url;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private boolean promo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Localization getLocalization() {
		return localization;
	}

	public void setLocalization(Localization localization) {
		this.localization = localization;
	}

	public String getFirstNumber() {
		return firstNumber;
	}

	public void setFirstNumber(String firstNumber) {
		this.firstNumber = firstNumber;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Ulid getSetId() {
		return setId;
	}

	public void setSetId(Ulid setId) {
		this.setId = setId;
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

	public String getPage2Name() {
		return page2Name;
	}

	public void setPage2Name(String page2Name) {
		this.page2Name = page2Name;
	}

	public String getH3Name() {
		return h3Name;
	}

	public void setH3Name(String h3Name) {
		this.h3Name = h3Name;
	}
}
