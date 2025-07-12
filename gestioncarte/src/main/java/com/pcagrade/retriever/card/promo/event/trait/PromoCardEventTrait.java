package com.pcagrade.retriever.card.promo.event.trait;

import com.pcagrade.retriever.card.TradingCardGame;
import com.pcagrade.retriever.card.promo.event.PromoCardEvent;
import com.pcagrade.retriever.card.promo.event.trait.translation.PromoCardEventTraitTranslation;
import com.pcagrade.retriever.localization.translation.AbstractTranslatableEntity;
import org.hibernate.envers.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "promo_trait")
@Audited
public class PromoCardEventTrait extends AbstractTranslatableEntity<PromoCardEventTrait, PromoCardEventTraitTranslation> {

    public static final String HOLO = "holo";
    public static final String STAMP = "stamp";
    public static final String EXCLUSIVE = "exclusive";
    public static final String PARENTHESIS = "parenthesis";
    public static final String EVENT = "event";
    public static final String YELLOW_ALTERNATE = "yellowAlternate";
    public static final String INCLUDED = "included";

    @Column
    private String type;

    @Column(name = "nom_parsable")
    private String name;

    @Column
    private TradingCardGame tcg;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "traits")
    @Audited
    private Set<PromoCardEvent> events = new HashSet<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<PromoCardEvent> getEvents() {
        return events;
    }

    public void setEvents(Set<PromoCardEvent> events) {
        this.events = events;
    }

    public TradingCardGame getTcg() {
        return tcg;
    }

    public void setTcg(TradingCardGame tcg) {
        this.tcg = tcg;
    }
}
