package com.pcagrade.retriever.card.yugioh.set;

import com.pcagrade.retriever.card.set.CardSet;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "yugioh_set")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("ygh")
public class YuGiOhSet extends CardSet {

    @Column(name = "id_pca")
    private Integer idPca;

    @Column
    private boolean promo;

    @Column
    private String type;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
