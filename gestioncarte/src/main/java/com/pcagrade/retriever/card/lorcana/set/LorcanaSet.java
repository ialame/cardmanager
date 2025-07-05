package com.pcagrade.retriever.card.lorcana.set;

import com.pcagrade.retriever.card.set.CardSet;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "lorcana_set")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("lor")
public class LorcanaSet extends CardSet {

    @Column(name = "num")
    private String number;
    @Column(name = "id_pca")
    private Integer idPca;
    @Column
    private boolean promo;


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getIdPca() {
        return idPca;
    }

    public void setIdPca(Integer idPca) {
        this.idPca = idPca;
    }

    public boolean isPromo() {
        return promo;
    }

    public void setPromo(boolean promo) {
        this.promo = promo;
    }
}
