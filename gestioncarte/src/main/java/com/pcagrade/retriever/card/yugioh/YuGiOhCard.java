package com.pcagrade.retriever.card.yugioh;

import com.pcagrade.retriever.card.Card;
import com.pcagrade.retriever.card.foil.Foil;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "yugioh_card")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("ygh")
@Audited
public class YuGiOhCard extends Card {

    @Column(name = "id_prim")
    private String idPrim;

    @Lob
    @Column(name = "types", columnDefinition = "LONGTEXT")
    private String types = "[]";

    @Column
    private int level;
    @Column
    private Foil foil = Foil.NON_FOIL_ONLY;

    @Column(name = "sneak_peek")
    private boolean sneakPeek = false;

    public String getIdPrim() {
        return idPrim;
    }

    public void setIdPrim(String idPrim) {
        this.idPrim = idPrim;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Foil getFoil() {
        return foil;
    }

    public void setFoil(Foil foil) {
        this.foil = foil;
    }

    public boolean isSneakPeek() {
        return sneakPeek;
    }

    public void setSneakPeek(boolean sneakPeek) {
        this.sneakPeek = sneakPeek;
    }
}
