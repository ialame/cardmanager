package com.pcagrade.retriever.alert;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AlertStatus {
	OPEN("open"),
	REFRESH("refresh"),
	CLOSED("closed"),
	REOPENED("reopened");
	
	private final String code;
	
	AlertStatus(String code) {
		this.code = code;
	}

	@JsonValue
	public String getCode() {
		return code;
	}

	public boolean isOpen() {
		return this == OPEN || this == REOPENED;
	}
}
