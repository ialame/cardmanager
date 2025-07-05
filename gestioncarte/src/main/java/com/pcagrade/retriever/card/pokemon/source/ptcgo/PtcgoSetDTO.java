package com.pcagrade.retriever.card.pokemon.source.ptcgo;

import io.swagger.v3.oas.annotations.media.Schema;

public class PtcgoSetDTO {

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private int id;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private String fileName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
