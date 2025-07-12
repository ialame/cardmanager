package com.pcagrade.retriever.card.promo.version;


import com.pcagrade.retriever.card.TradingCardGame;
import com.pcagrade.retriever.card.promo.PromoCard;
import com.pcagrade.retriever.card.promo.version.translation.PromoCardVersionTranslation;
import com.pcagrade.retriever.localization.translation.AbstractTranslatableEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "version")
@Audited
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PromoCardVersion extends AbstractTranslatableEntity<PromoCardVersion, PromoCardVersionTranslation> {

    @Column(name = "nom")
    private String name;

    @Column
    private Boolean hidden;

    @Column
    private TradingCardGame tcg;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "version")
    @Audited
    private Set<PromoCard> promoCards = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Set<PromoCard> getPromoCards() {
        return promoCards;
    }

    public void setPromoCards(Set<PromoCard> promoCards) {
        this.promoCards = promoCards;
    }

    public TradingCardGame getTcg() {
        return tcg;
    }

    public void setTcg(TradingCardGame tcg) {
        this.tcg = tcg;
    }
}
