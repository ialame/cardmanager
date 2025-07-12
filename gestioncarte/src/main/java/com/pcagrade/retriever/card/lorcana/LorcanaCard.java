package com.pcagrade.retriever.card.lorcana;

import com.pcagrade.retriever.card.Card;
import org.hibernate.envers.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "lorcana_card")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("lor")
@Audited
public class LorcanaCard extends Card {

    @Column
    private String rarity;
    @Column
    private String ink;
    @Column
    private boolean reprint;
    @Column(name = "id_prim")
    private String idPrim;


    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getInk() {
        return ink;
    }

    public void setInk(String ink) {
        this.ink = ink;
    }

    public boolean isReprint() {
        return reprint;
    }

    public void setReprint(boolean reprint) {
        this.reprint = reprint;
    }

    public String getIdPrim() {
        return idPrim;
    }

    public void setIdPrim(String idPrim) {
        this.idPrim = idPrim;
    }
}
