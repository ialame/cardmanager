package com.pcagrade.retriever.card.order.history;

import com.pcagrade.retriever.card.certification.CardCertification;
import com.pcagrade.retriever.card.order.CardOrder;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_history")
public class OrderHistory extends AbstractUlidEntity {

	@ManyToOne
	@JoinColumn(name = "order_id")
	private CardOrder cardOrder;

	@ManyToOne
	@JoinColumn(name = "card_certification_id")
	private CardCertification certification;


	public CardCertification getCertification() {
		return certification;
	}

	public void setCertification(CardCertification certification) {
		this.certification = certification;
	}

	public CardOrder getCardOrder() {
		return cardOrder;
	}

	public void setCardOrder(CardOrder cardOrder) {
		this.cardOrder = cardOrder;
	}

}
