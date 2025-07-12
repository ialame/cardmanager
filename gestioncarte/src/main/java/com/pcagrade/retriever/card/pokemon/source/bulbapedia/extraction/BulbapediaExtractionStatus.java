package com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BulbapediaExtractionStatus {
    OK("ok"),
    NO_PAGE_2("no_page_2"),
    NOT_IN_PAGE_1("not_in_page_1"),
    NOT_IN_PAGE_2("not_in_page_2"),
    NO_SET("no_set");

    private final String code;

    BulbapediaExtractionStatus(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static BulbapediaExtractionStatus getByCode(String code) {
        for (BulbapediaExtractionStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return OK;
    }
}
