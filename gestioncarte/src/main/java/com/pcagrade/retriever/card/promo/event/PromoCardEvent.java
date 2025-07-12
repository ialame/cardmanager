package com.pcagrade.retriever.card.promo.event;

import com.pcagrade.retriever.card.promo.PromoCard;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTrait;
import com.pcagrade.retriever.card.promo.event.translation.PromoCardEventTranslation;
import com.pcagrade.retriever.localization.translation.AbstractTranslatableEntity;
import org.hibernate.envers.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "promo_card_event")
@Audited
public class PromoCardEvent extends AbstractTranslatableEntity<PromoCardEvent, PromoCardEventTranslation> {
    @Column(name = "nom", nullable = false, length = 200)
    private String name;

    @Column(name = "hidden", nullable = false)
    private Boolean hidden = false;

    @Column(name = "championship", nullable = false)
    private Boolean championship = false;

    @Deprecated(forRemoval = true)
    @Column(name = "date_sortie")
    private LocalDateTime releaseDate;

    @Column(name = "without_date", nullable = false)
    private Boolean withoutDate = false;

    @Column(name = "always_displayed", nullable = false)
    private Boolean alwaysDisplayed = false;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "event")
    @Audited
    private Set<PromoCard> promoCards = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "evenements_caracteristiques",
            joinColumns = @JoinColumn(name = "promo_card_event_id"),
            inverseJoinColumns = @JoinColumn(name = "promo_trait_id"))
    @Audited
    private Set<PromoCardEventTrait> traits = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String nom) {
        this.name = nom;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Boolean getChampionship() {
        return championship;
    }

    public void setChampionship(Boolean championship) {
        this.championship = championship;
    }

    @Deprecated(forRemoval = true)
    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    @Deprecated(forRemoval = true)
    public void setReleaseDate(LocalDateTime dateSortie) {
        this.releaseDate = dateSortie;
    }

    public Boolean getWithoutDate() {
        return withoutDate;
    }

    public void setWithoutDate(Boolean withoutDate) {
        this.withoutDate = withoutDate;
    }

    public Boolean getAlwaysDisplayed() {
        return alwaysDisplayed;
    }

    public void setAlwaysDisplayed(Boolean alwaysDisplayed) {
        this.alwaysDisplayed = alwaysDisplayed;
    }

    public Set<PromoCard> getPromoCards() {
        return promoCards;
    }

    public void setPromoCards(Set<PromoCard> promoCards) {
        this.promoCards = promoCards;
    }

    public Set<PromoCardEventTrait> getTraits() {
        return traits;
    }

    public void setTraits(Set<PromoCardEventTrait> traits) {
        this.traits = traits;
    }
}
