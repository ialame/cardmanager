package com.pcagrade.retriever.card.artist;

import com.pcagrade.retriever.card.Card;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "card_artist")
public class CardArtist extends AbstractUlidEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "artist")
    private Set<Card> cards = new LinkedHashSet<>();

    public CardArtist() {

    }

    public CardArtist(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Card> getCards() {
        return cards;
    }

    public void setCards(Set<Card> cards) {
        this.cards = cards;
    }
}
