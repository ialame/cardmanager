package com.pcagrade.retriever.card.promo.event.trait.translation;

import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTrait;
import com.pcagrade.retriever.localization.translation.AbstractTranslationEntity;
import org.hibernate.envers.Audited;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "promo_trait_translation", uniqueConstraints = { @UniqueConstraint(columnNames = { "id", "locale" }) })
@AttributeOverride(name = "name", column = @Column(name = "nom_complet"))
@Audited
@Cacheable
public class PromoCardEventTraitTranslation extends AbstractTranslationEntity<PromoCardEventTrait> {

    @Column(name = "nom_zebra")
    private String labelName;

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }
}
