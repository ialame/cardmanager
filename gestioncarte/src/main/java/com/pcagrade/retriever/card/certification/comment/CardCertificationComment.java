package com.pcagrade.retriever.card.certification.comment;

import com.pcagrade.retriever.card.certification.CardCertification;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "card_certification_comment")
public class CardCertificationComment extends AbstractUlidEntity {


	@ManyToOne
	@JoinColumn(name = "card_certification_id")
	private CardCertification certification;

	public CardCertification getCertification() {
		return certification;
	}

	public void setCertification(CardCertification certification) {
		this.certification = certification;
	}
}
