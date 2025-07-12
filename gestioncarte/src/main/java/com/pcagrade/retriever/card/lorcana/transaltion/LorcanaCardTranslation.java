package com.pcagrade.retriever.card.lorcana.transaltion;

import com.pcagrade.retriever.card.CardTranslation;
import org.hibernate.envers.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "lorcana_card_translation")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("lor")
@Audited
public class LorcanaCardTranslation extends CardTranslation {

    @Column(name = "num")
    private String number;
    @Column(name = "full_number")
    private String fullNumber;
    @Column
    private String title = "";

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFullNumber() {
        return fullNumber;
    }

    public void setFullNumber(String fullNumber) {
        this.fullNumber = fullNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
