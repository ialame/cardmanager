package com.pcagrade.retriever.card.pokemon.feature.translation;

import com.pcagrade.retriever.card.pokemon.feature.Feature;
import com.pcagrade.retriever.localization.translation.AbstractTranslationEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;

@Entity
@Table(name = "pokemon_card_feature_translation")
@AttributeOverride(name = "name", column = @Column(name = "nom_complet"))
@SecondaryTable(name="j_extension__pokemon_card_feature_translation", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id"))
@Audited
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FeatureTranslation extends AbstractTranslationEntity<Feature> {

    @Column(name = "nom_zebra")
    private String zebraName;

    @Column(name = "verification_pattern", table = "j_extension__pokemon_card_feature_translation")
    private String verificationPattern;
    @Column(name = "label_verification_pattern", table = "j_extension__pokemon_card_feature_translation")
    private String labelVerificationPattern;

    public String getZebraName() {
        return zebraName;
    }

    public void setZebraName(String zebraName) {
        this.zebraName = zebraName;
    }

    public String getVerificationPattern() {
        return verificationPattern;
    }

    public void setVerificationPattern(String verificationPattern) {
        this.verificationPattern = verificationPattern;
    }

    public String getLabelVerificationPattern() {
        return labelVerificationPattern;
    }

    public void setLabelVerificationPattern(String labelVerificationPattern) {
        this.labelVerificationPattern = labelVerificationPattern;
    }
}
