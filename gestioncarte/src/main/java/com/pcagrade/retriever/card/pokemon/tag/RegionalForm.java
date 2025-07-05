package com.pcagrade.retriever.card.pokemon.tag;

public enum RegionalForm {

    ALOLA("alola"),
    GALAR("galar"),
    HISUI("hisui"),
    PALDEA("paldea");

    private final String name;

    RegionalForm(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
