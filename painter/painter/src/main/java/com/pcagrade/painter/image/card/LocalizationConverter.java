package com.pcagrade.painter.image.card;

import com.pcagrade.mason.localization.Localization;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocalizationConverter {

    default Localization stringToLocalization(String localization) {
        if (localization == null || localization.isEmpty()) {
            return Localization.FRANCE; // Valeur par défaut
        }
        try {
            return Localization.valueOf(localization.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Localization.FRANCE; // Valeur par défaut en cas d'erreur
        }
    }

    default String localizationToString(Localization localization) {
        if (localization == null) {
            return "fr";
        }
        return localization.name().toLowerCase();
    }
}