package com.pcagrade.retriever.card.yugioh.set.translation;

import com.pcagrade.retriever.card.set.CardSetTranslation;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "yugioh_set_translation")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("ygh")
public class YuGiOhSetTranslation extends CardSetTranslation {

    @Column
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
