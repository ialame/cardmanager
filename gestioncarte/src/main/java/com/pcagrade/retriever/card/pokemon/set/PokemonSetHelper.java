package com.pcagrade.retriever.card.pokemon.set;

import com.pcagrade.mason.localization.Localization;

public class PokemonSetHelper {

    public static Localization getSetLocalization(PokemonSetDTO set) {
        var translations = set.getTranslations();
        var translation = translations.get(Localization.USA);

        if (translation != null && translation.isAvailable()) {
            return Localization.USA;
        }

        translation = translations.get(Localization.JAPAN);

        if (translation != null && translation.isAvailable()) {
            return Localization.JAPAN;
        }
        return null;
    }
}
