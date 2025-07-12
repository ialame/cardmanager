package com.pcagrade.painter.image.card;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import com.pcagrade.mason.ulid.jpa.UlidColumnDefinitions;
import com.pcagrade.mason.ulid.jpa.UlidType;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "card_image", indexes = {
        @Index(name = "card_image_card_id_idx", columnList = "card_id")
})
public class CardImage extends AbstractUlidEntity {  // SUPPRIMÉ: implements ILocalized

    @Column(name = "set_source_id", columnDefinition = UlidColumnDefinitions.DEFINITION)
    @Type(UlidType.class)
    private Ulid setSourceId;

    @Column(name = "card_id", columnDefinition = UlidColumnDefinitions.DEFINITION)
    @Type(UlidType.class)
    private Ulid cardId;

    @Column(name = "set_id", columnDefinition = UlidColumnDefinitions.DEFINITION)
    @Type(UlidType.class)
    private Ulid setId;

    @Column(name = "langue", nullable = false)
    private String langue;

    @Column(name = "fichier", nullable = false, unique = true)
    private String fichier;

    @Column(name = "traits", nullable = false, columnDefinition = "longtext")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> traits = new HashMap<>();

    @Column(name = "statut", nullable = false)
    private Integer statut = 0;

    @Column(name = "infos", nullable = false, columnDefinition = "longtext")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> infos = new HashMap<>();

    @Column(name = "downloaded_at", nullable = false)
    private Instant downloadedAt = Instant.now();

    @Column(name = "taille_img", length = 50)
    private String tailleImg;

    @Column(name = "cards")
    private String cards;

    @Column(name = "src", unique = true)
    private String src;

    // Garder String pour la localization
    @Column(name = "localization", length = 255, columnDefinition = "varchar(255) DEFAULT 'fr'")
    private String localization = "fr";

    // Constructeurs
    public CardImage() {
        super();
        this.traits = new HashMap<>();
        this.infos = new HashMap<>();
        this.downloadedAt = Instant.now();
        this.statut = 0;
        this.fichier = "";
        this.langue = "";
        this.localization = "fr";
    }

    // Getters et setters (tous les getters/setters existants + ceux pour localization)
    public String getLocalization() {
        return localization;
    }

    public void setLocalization(String localization) {
        this.localization = localization != null ? localization : "fr";
    }


    public Ulid getSetSourceId() {
        return setSourceId;
    }

    public void setSetSourceId(Ulid setSourceId) {
        this.setSourceId = setSourceId;
    }

    public Ulid getCardId() {
        return cardId;
    }

    public void setCardId(Ulid cardId) {
        this.cardId = cardId;
    }

    public Ulid getSetId() {
        return setId;
    }

    public void setSetId(Ulid setId) {
        this.setId = setId;
    }

    public String getLangue() {
        return langue;
    }

    public void setLangue(String langue) {
        this.langue = langue;
    }

    public String getFichier() {
        return fichier;
    }

    public void setFichier(String fichier) {
        this.fichier = fichier;
    }

    public Map<String, Object> getTraits() {
        return traits;
    }

    public void setTraits(Map<String, Object> traits) {
        this.traits = traits;
    }

    public Integer getStatut() {
        return statut;
    }

    public void setStatut(Integer statut) {
        this.statut = statut;
    }

    public Map<String, Object> getInfos() {
        return infos;
    }

    public void setInfos(Map<String, Object> infos) {
        this.infos = infos;
    }

    public Instant getDownloadedAt() {
        return downloadedAt;
    }

    public void setDownloadedAt(Instant downloadedAt) {
        this.downloadedAt = downloadedAt;
    }

    public String getTailleImg() {
        return tailleImg;
    }

    public void setTailleImg(String tailleImg) {
        this.tailleImg = tailleImg;
    }

    public String getCards() {
        return cards;
    }

    public void setCards(String cards) {
        this.cards = cards;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
}