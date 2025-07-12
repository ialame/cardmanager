package com.pcagrade.retriever.card.tag.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CardTagType {
    FORME("forme"),
    REGIONAL_FORM("regional_form"),
    TERA_TYPE("tera_type");

    private final String code;

    CardTagType(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static CardTagType getByCode(String code) {
        for (CardTagType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }

}
