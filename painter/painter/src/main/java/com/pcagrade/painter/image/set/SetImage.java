package com.pcagrade.painter.image.set;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.localization.ILocalized;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.localization.jpa.LocalizationColumnDefinitions;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import com.pcagrade.mason.ulid.jpa.UlidColumnDefinitions;
import com.pcagrade.mason.ulid.jpa.UlidType;
import com.pcagrade.painter.image.Image;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Table(name = "set_image", indexes = {
        @Index(name = "set_image_set_id_idx", columnList = "set_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "set_image_set_id_localization_uq", columnNames = {"set_id", "localization"})
})
@Audited
public class SetImage extends AbstractUlidEntity implements ILocalized {

    @Column(name = "set_id", nullable = false, columnDefinition = UlidColumnDefinitions.DEFINITION)
    @Type(UlidType.class)
    private Ulid setId;

    @Column(nullable = false, columnDefinition = LocalizationColumnDefinitions.DEFINITION)
    private Localization localization;

    @ManyToOne
    @JoinColumn(name = "image_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Image image;

    public Ulid getSetId() {
        return setId;
    }

    public void setSetId(Ulid setId) {
        this.setId = setId;
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
}
