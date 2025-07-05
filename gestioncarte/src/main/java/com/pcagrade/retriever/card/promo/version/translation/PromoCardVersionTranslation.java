package com.pcagrade.retriever.card.promo.version.translation;

import com.pcagrade.retriever.card.promo.version.PromoCardVersion;
import com.pcagrade.retriever.localization.translation.AbstractTranslationEntity;
import org.hibernate.envers.Audited;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "version_translation")
@AttributeOverride(name = "name", column = @Column(name = "nom_complet"))
@Audited
@Cacheable
public class PromoCardVersionTranslation extends AbstractTranslationEntity<PromoCardVersion> {
    @Column(name = "nom_zebra")
    private String labelName;

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }
}
