package com.pcagrade.retriever.card.onepiece.set;

import com.pcagrade.retriever.card.onepiece.source.official.id.OnePieceOfficialSiteId;
import com.pcagrade.retriever.card.set.CardSet;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "one_piece_set")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("onp")
public class OnePieceSet extends CardSet {

    @Column(name = "id_pca")
    private Integer idPca;
    @Column
    private boolean promo;
    @Column
    private String code;

    @OneToMany(mappedBy = "set", cascade = CascadeType.ALL)
    private List<OnePieceOfficialSiteId> officialSiteIds = new ArrayList<>();

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<OnePieceOfficialSiteId> getOfficialSiteIds() {
        return officialSiteIds;
    }

    public void setOfficialSiteIds(List<OnePieceOfficialSiteId> officialSiteIds) {
        this.officialSiteIds = officialSiteIds;
    }
}
