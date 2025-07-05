package com.pcagrade.painter.image.card;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.localization.ILocalized;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.localization.jpa.LocalizationColumnDefinitions;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import com.pcagrade.mason.ulid.jpa.UlidColumnDefinitions;
import com.pcagrade.mason.ulid.jpa.UlidType;
import com.pcagrade.painter.image.Image;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "card_image", indexes = {
        @Index(name = "card_image_card_id_idx", columnList = "card_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "card_image_card_id_localization_uq", columnNames = {"card_id", "localization"})
})
@Audited
public class CardImage extends AbstractUlidEntity implements ILocalized {

    @Column(name = "card_id", nullable = false, columnDefinition = UlidColumnDefinitions.DEFINITION)
    @Type(UlidType.class)
    private Ulid cardId;

    @Column(name = "langue",nullable = false, columnDefinition = LocalizationColumnDefinitions.DEFINITION)
    private Localization localization;

    @ManyToOne
    @JoinColumn(name = "image_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Image image;

//    @OneToMany(mappedBy = "image", cascade = CascadeType.ALL)
//    private List<CardImageHistory> history;


    @Column(name = "fichier", nullable = false)
    private String fichier = "toto";

    @Column(name = "traits", nullable = false, columnDefinition = "longtext")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> traits=Collections.emptyMap();

    @Column(name = "statut", nullable = false)
    private Integer statut=0;

    @Column(name = "infos", nullable = false, columnDefinition = "longtext")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> infos= Collections.emptyMap();

    @Column(name = "downloaded_at", nullable = false)
    private Instant downloadedAt = Instant.now();

    @Column(name = "taille_img", length = 50)
    private String tailleImg;

    @Column(name = "cards")
    private String cards;

    @Column(name = "src")
    private String src;


    public Ulid getCardId() {
        return cardId;
    }

    public void setCardId(Ulid cardId) {
        this.cardId = cardId;
    }

    @Override
    public Localization getLocalization() {
        return localization;
    }

    @Override
    public void setLocalization(Localization localization) {
        this.localization = localization;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    /// ///////////////////////////////////////////////////////////

















}
