package com.pcagrade.retriever.card.pokemon.serie;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.pokemon.serie.translation.PokemonSerieTranslation;
import com.pcagrade.mason.localization.Localization;

public class PokemonSerieTestProvider {

    public static final Ulid XY_ID = Ulid.from("01G4GFP9Z2JEGRJPYWXZ9TCFBV");

    public static PokemonSerie xy() {
        var serie = new PokemonSerie();
        var fr = new PokemonSerieTranslation();
        var us = new PokemonSerieTranslation();

        serie.setId(XY_ID);
        serie.setIdPca(8);
        fr.setName("XY");
        fr.setCode("XY");
        fr.setLocalization(Localization.FRANCE);
        serie.setTranslation(Localization.FRANCE, fr);
        us.setName("XY");
        us.setCode("XY");
        us.setLocalization(Localization.USA);
        serie.setTranslation(Localization.USA, us);
        return serie;
    }

}
