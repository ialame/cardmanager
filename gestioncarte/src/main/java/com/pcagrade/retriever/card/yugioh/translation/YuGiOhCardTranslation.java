package com.pcagrade.retriever.card.yugioh.translation;

import com.pcagrade.retriever.card.CardTranslation;
import org.hibernate.envers.Audited;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "yugioh_card_translation")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("ygh")
@Audited
@Cacheable
public class YuGiOhCardTranslation extends CardTranslation {

    @Column(name = "num")
    private String number;
    @Column
    private String rarity;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }
}
