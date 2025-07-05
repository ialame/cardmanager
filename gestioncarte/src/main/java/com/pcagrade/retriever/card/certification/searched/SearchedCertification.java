package com.pcagrade.retriever.card.certification.searched;

import com.pcagrade.retriever.card.certification.CardCertification;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "searched_certification")
public class SearchedCertification extends AbstractUlidEntity {


	@ManyToOne
	@JoinColumn(name = "cardcertification_id")
	private CardCertification certification;


	public CardCertification getCertification() {
		return certification;
	}

	public void setCertification(CardCertification certification) {
		this.certification = certification;
	}
}
