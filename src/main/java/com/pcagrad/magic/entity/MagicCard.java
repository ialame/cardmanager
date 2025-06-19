package com.pcagrad.magic.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcagrad.magic.util.Localization;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Getter @Setter  // Remplace @Data pour éviter les conflits
@Entity
@Table(name = "magic_card")
public class MagicCard extends Card {

    // ===== CHAMPS EXISTANTS BASÉS SUR VOTRE STRUCTURE DE TABLE =====

    @Size(max = 20)
    @Column(name="id_prim", length = 20)
    private String idPrim;

    @Column(name = "numero")
    private Integer numero;

    @Size(max = 50)
    @Column(name = "fusion_pca", length = 50)
    private String fusionPca;

    @Size(max = 100)
    @Column(name = "color_identity", length = 100)
    private String colorIdentity;

    @Size(max = 100)
    @Column(name = "types", length = 100)
    private String types;

    @Size(max = 100)
    @Column(name = "colors", length = 100)
    private String colors;

    @Size(max = 100)
    @Column(name = "layout", length = 100)
    private String layout;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "has_img", nullable = false)
    private Boolean hasImg = false;

    @Column(name = "has_foil")
    private Boolean hasFoil;

    @Column(name = "has_non_foil")
    private Boolean hasNonFoil;

    @Size(max = 100)
    @Column(name = "rarity", length = 100)
    private String rarity;

    @Column(name = "is_foil_only")
    private Boolean isFoilOnly;

    @Column(name = "is_online_only")
    private Boolean isOnlineOnly;

    @Column(name = "is_oversized")
    private Boolean isOversized;

    @Column(name = "is_timeshifted")
    private Boolean isTimeshifted;

    @Size(max = 100)
    @Column(name = "side", length = 100)
    private String side;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "certifiable", nullable = false)
    private Boolean certifiable = false;

    @Column(name = "is_token")
    private Boolean isToken;

    @Column(name = "filtre")
    private Integer filtre;

    @Column(name = "is_reclassee")
    private Boolean isReclassee;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "has_recherche", nullable = false)
    private Boolean hasRecherche = false;

    @Size(max = 50)
    @Column(name = "z_post_extension", length = 50)
    private String zPostExtension;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "has_date_fr", nullable = false)
    private Boolean hasDateFr = false;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "is_affichable", nullable = false)
    private Boolean isAffichable = false;

    // ===== MÉTHODES PERSONNALISÉES (remplacent les getters/setters Lombok pour certains champs) =====

    /**
     * Override des getters/setters pour colors - gère à la fois String et List<String>
     */
    public String getColors() {
        return this.colors;
    }

    public void setColors(String colors) {
        this.colors = colors;
    }

    // MÉTHODE ADDITIONNELLE pour retourner une liste (pour compatibilité avec MtgCard)
    public List<String> getColorsList() {
        return parseStringToList(this.colors);
    }

    public void setColorsList(List<String> colorsList) {
        this.colors = listToString(colorsList);
    }

    /**
     * Override des getters/setters pour colorIdentity - gère à la fois String et List<String>
     */
    public String getColorIdentity() {
        return this.colorIdentity;
    }

    public void setColorIdentity(String colorIdentity) {
        this.colorIdentity = colorIdentity;
    }

    // MÉTHODE ADDITIONNELLE pour retourner une liste (pour compatibilité avec MtgCard)
    public List<String> getColorIdentityList() {
        return parseStringToList(this.colorIdentity);
    }

    public void setColorIdentityList(List<String> colorIdentityList) {
        this.colorIdentity = listToString(colorIdentityList);
    }

    /**
     * Override des getters/setters pour types - gère à la fois String et List<String>
     */
    public String getTypes() {
        return this.types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    // MÉTHODE ADDITIONNELLE pour retourner une liste (pour compatibilité avec MtgCard)
    public List<String> getTypesList() {
        return parseStringToList(this.types);
    }

    public void setTypesList(List<String> typesList) {
        this.types = listToString(typesList);
    }

    // ===== MÉTHODES POUR LA COMPATIBILITÉ AVEC L'ANCIEN CODE =====

    /**
     * Méthodes pour le téléchargement d'images
     */
    public Boolean getImageDownloaded() {
        return this.hasImg != null ? this.hasImg : false;
    }

    public void setImageDownloaded(Boolean downloaded) {
        this.hasImg = downloaded;
    }

    public void setImageDownloaded(boolean downloaded) {
        this.hasImg = downloaded;
    }

    /**
     * Méthode pour obtenir le nom de l'extension
     */
    public String getSetName() {
        String setName = extractFromAttributes("setName", "");
        if (!setName.isEmpty()) {
            return setName;
        }
        return this.zPostExtension != null ? this.zPostExtension : "Extension inconnue";
    }

    /**
     * Méthode pour obtenir l'ID externe stocké dans idPrim
     */
    public String getExternalId() {
        return this.idPrim;
    }

    public void setExternalId(String externalId) {
        this.idPrim = externalId;
    }

    /**
     * Méthode pour obtenir le code de l'extension
     */
    public String getSetCode() {
        return this.zPostExtension;
    }

    /**
     * Méthode pour obtenir le nom de la carte (depuis les traductions)
     */
    public String getName() {
        CardTranslation usaTranslation = getTranslation(Localization.USA);
        if (usaTranslation != null && usaTranslation.getName() != null) {
            return usaTranslation.getName();
        }

        return getTranslations().stream()
                .filter(t -> t.getName() != null && !t.getName().trim().isEmpty())
                .map(CardTranslation::getName)
                .findFirst()
                .orElse("Carte sans nom");
    }

    /**
     * Méthodes d'accès aux données MTG stockées dans les champs JSON
     */
    public String getManaCost() {
        return extractFromAttributes("manaCost", "");
    }

    public Integer getCmc() {
        String cmc = extractFromAttributes("cmc", "0");
        try {
            return Integer.parseInt(cmc);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public String getType() {
        return extractFromAttributes("type", "");
    }

    public String getText() {
        return extractFromAttributes("text", "");
    }

    public String getPower() {
        return extractFromAttributes("power", "");
    }

    public String getToughness() {
        return extractFromAttributes("toughness", "");
    }

    public String getArtist() {
        return extractFromAttributes("artist", "");
    }

    public String getMultiverseid() {
        return extractFromAttributes("multiverseid", "");
    }

    /**
     * Méthodes pour les listes stockées dans JSON
     */
    public List<String> getSupertypes() {
        return extractListFromAllowedNotes("supertypes", new ArrayList<>());
    }

    public void setSupertypes(List<String> supertypesList) {
        storeListInAllowedNotes("supertypes", supertypesList);
    }

    public List<String> getSubtypes() {
        return extractListFromAllowedNotes("subtypes", new ArrayList<>());
    }

    public void setSubtypes(List<String> subtypesList) {
        storeListInAllowedNotes("subtypes", subtypesList);
    }

    /**
     * Pour la compatibilité avec l'URL d'image (stockée dans attributes)
     */
    public String getOriginalImageUrl() {
        return extractFromAttributes("imageUrl", "");
    }

    public void setOriginalImageUrl(String imageUrl) {
        storeInAttributes("imageUrl", imageUrl);
    }

    public String getLocalImagePath() {
        return extractFromAttributes("localImagePath", "");
    }

    public void setLocalImagePath(String localPath) {
        storeInAttributes("localImagePath", localPath);
    }

    // ===== MÉTHODES UTILITAIRES PRIVÉES =====

    /**
     * Convertit une chaîne séparée par des virgules en liste
     */
    private List<String> parseStringToList(String str) {
        if (str == null || str.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(str.split(",\\s*"));
    }

    /**
     * Convertit une liste en chaîne séparée par des virgules
     */
    private String listToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return String.join(",", list);
    }

    /**
     * Extrait une valeur du JSON attributes hérite de Card
     */
    private String extractFromAttributes(String key, String defaultValue) {
        try {
            if (getAttributes() != null && !getAttributes().trim().isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> attributesMap = mapper.readValue(getAttributes(), Map.class);
                Object value = attributesMap.get(key);
                return value != null ? value.toString() : defaultValue;
            }
        } catch (Exception e) {
            // Log error silently and return default
        }
        return defaultValue;
    }

    /**
     * Stocke une valeur dans le JSON attributes hérite de Card
     */
    private void storeInAttributes(String key, String value) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> attributesMap;

            if (getAttributes() != null && !getAttributes().trim().isEmpty()) {
                attributesMap = mapper.readValue(getAttributes(), Map.class);
            } else {
                attributesMap = new java.util.HashMap<>();
            }

            attributesMap.put(key, value);
            setAttributes(mapper.writeValueAsString(attributesMap));
        } catch (Exception e) {
            // Log error silently
        }
    }

    /**
     * Extrait une liste du JSON allowedNotes hérite de Card
     */
    @SuppressWarnings("unchecked")
    private List<String> extractListFromAllowedNotes(String key, List<String> defaultValue) {
        try {
            if (getAllowedNotes() != null && !getAllowedNotes().trim().isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> notesMap = mapper.readValue(getAllowedNotes(), Map.class);
                Object value = notesMap.get(key);
                if (value instanceof List) {
                    return (List<String>) value;
                }
            }
        } catch (Exception e) {
            // Log error silently and return default
        }
        return defaultValue;
    }

    /**
     * Stocke une liste dans le JSON allowedNotes hérite de Card
     */
    private void storeListInAllowedNotes(String key, List<String> list) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> notesMap;

            if (getAllowedNotes() != null && !getAllowedNotes().trim().isEmpty()) {
                notesMap = mapper.readValue(getAllowedNotes(), Map.class);
            } else {
                notesMap = new java.util.HashMap<>();
            }

            notesMap.put(key, list);
            setAllowedNotes(mapper.writeValueAsString(notesMap));
        } catch (Exception e) {
            // Log error silently
        }
    }
    public Integer getMultiverseidAsInteger() {
        String multiverseid = extractFromAttributes("multiverseid", "");
        if (multiverseid.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(multiverseid);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}