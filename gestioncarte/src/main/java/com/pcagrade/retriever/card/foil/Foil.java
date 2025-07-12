package com.pcagrade.retriever.card.foil;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Foil {
    NON_FOIL_ONLY("non_foil_only"),
    FOIL_ONLY("foil_only"),
    FOIL("foil"),
    NON_FOIL("non_foil");


    private final String name;

    Foil(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static Foil getByName(String name) {
        for (Foil foil : values()) {
            if (foil.name.equals(name)) {
                return foil;
            }
        }
        return NON_FOIL_ONLY;
    }
}
