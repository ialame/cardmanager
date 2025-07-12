package com.pcagrade.retriever.card.pokemon.source.ptcgo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PtcgoTranslation {

	private String id;
	private String setCode;
	
	private String number;
	private String name;
	
	public String getId() {
		return id;
	}

	public String getSetCode() {
		return setCode;
	}

	public String getNumber() {
		return number;
	}

	public String getName() {
		return name;
	}
	
}
