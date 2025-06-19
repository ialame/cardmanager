package com.pcagrad.magic.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Ajouts à faire dans MtgCard.java (record)

/**
 * Ajouts à faire dans votre record MtgCard.java
 * pour être compatible avec CardPersistenceService
 */

// Ajouter ces méthodes dans le record MtgCard (après la déclaration des champs)
public record MtgCard(
        String id,
        String name,
        @JsonProperty("manaCost") String manaCost,
        Integer cmc,
        List<String> colors,
        @JsonProperty("colorIdentity") List<String> colorIdentity,
        String type,
        List<String> supertypes,
        List<String> types,
        List<String> subtypes,
        String rarity,
        String set,
        @JsonProperty("setName") String setName,
        String text,
        String artist,
        String number,
        String power,
        String toughness,
        String layout,
        @JsonProperty("multiverseid") Integer multiverseid,
        @JsonProperty("imageUrl") String imageUrl
) {

    // Méthodes utilitaires pour la compatibilité avec l'ancien code

    /**
     * Obtenir la liste des couleurs (déjà disponible)
     */
    public List<String> colors() {
        return colors != null ? colors : new ArrayList<>();
    }

    /**
     * Obtenir l'identité des couleurs (déjà disponible)
     */
    public List<String> colorIdentity() {
        return colorIdentity != null ? colorIdentity : new ArrayList<>();
    }

    /**
     * Obtenir les supertypes (déjà disponible)
     */
    public List<String> supertypes() {
        return supertypes != null ? supertypes : new ArrayList<>();
    }

    /**
     * Obtenir les types (déjà disponible)
     */
    public List<String> types() {
        return types != null ? types : new ArrayList<>();
    }

    /**
     * Obtenir les sous-types (déjà disponible)
     */
    public List<String> subtypes() {
        return subtypes != null ? subtypes : new ArrayList<>();
    }

    /**
     * Note: foreignNames() n'est PAS disponible dans ce model simplifié
     * Si vous avez besoin de traductions, il faudrait étendre le record
     * ou utiliser une source de données différente
     */
}

