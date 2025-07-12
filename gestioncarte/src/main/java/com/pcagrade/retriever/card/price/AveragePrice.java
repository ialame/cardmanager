package com.pcagrade.retriever.card.price;

import com.pcagrade.retriever.card.Card;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "moyenne_prix")
public class AveragePrice extends AbstractUlidEntity {

	@OneToOne
	@JoinColumn(name = "card_id")
	private Card card;

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card.setPrice(null);
		this.card = card;
	}

}
