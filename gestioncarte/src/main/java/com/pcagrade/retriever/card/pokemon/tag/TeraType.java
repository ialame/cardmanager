package com.pcagrade.retriever.card.pokemon.tag;

public enum TeraType {

    GRASS("grass"),
    FIRE("fire"),
    WATER("water"),
    LIGHTNING("lightning"),
    PSYCHIC("psychic"),
    FIGHTING("fighting"),
    DARKNESS("darkness"),
    METAL("metal"),
    DRAGON("dragon"),
    FAIRY("fairy"),
    COLORLESS("colorless");

    private final String name;

    TeraType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
