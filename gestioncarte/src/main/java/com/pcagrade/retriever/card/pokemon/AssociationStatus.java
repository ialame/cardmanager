package com.pcagrade.retriever.card.pokemon;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AssociationStatus {

	NO_ASSOCIATION("no"),
	IN_DATABASE("in_db"),
	IN_TWO_LINES("in_2_lines"),
	NO_SET("no_set"),
	NOT_IN_DATABASE("not_in_db");

	private final String code;

	AssociationStatus(String code) {
		this.code = code;
	}

	@JsonValue
	public String getCode() {
		return code;
	}

	@JsonCreator
	public static AssociationStatus getByCode(String code) {
		for (AssociationStatus status : values()) {
			if (status.code.equals(code)) {
				return status;
			}
		}
		return NO_ASSOCIATION;
	}
}
