package com.pcagrade.retriever.card.onepiece;

import com.pcagrade.retriever.card.Card;
import org.hibernate.envers.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "one_piece_card")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("onp")
@Audited
public class OnePieceCard extends Card {

    @Column(name = "id_prim")
    private String idPrim;

    @Column
    private String type = "";

    @Column
    private String attribute;
    @Column
    private String color;
    @Column
    private String rarity;

    @Column
    private int parallel;


    public String getIdPrim() {
        return idPrim;
    }

    public void setIdPrim(String idPrim) {
        this.idPrim = idPrim;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public int getParallel() {
        return parallel;
    }

    public void setParallel(int parallel) {
        this.parallel = parallel;
    }
}
