package com.pcagrade.retriever.card.extraction.history;

import com.pcagrade.retriever.card.set.CardSet;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "j_card_extraction_history")
public class CardExtractionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToMany
    @JoinTable(name = "j_card_extraction_history_card_set", joinColumns = @JoinColumn(name = "history_id"), inverseJoinColumns = @JoinColumn(name = "card_set_id"))
    private Set<CardSet> cardSets = new HashSet<>();

    @Lob
    @Column(name = "card", columnDefinition = "LONGTEXT")
    private String card;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Set<CardSet> getCardSets() {
        return cardSets;
    }

    public void setCardSets(Set<CardSet> cardSets) {
        this.cardSets = cardSets;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }
}
