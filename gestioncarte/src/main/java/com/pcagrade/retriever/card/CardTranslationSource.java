package com.pcagrade.retriever.card;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "j_card_translation_source")
public class CardTranslationSource {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToOne
	@JoinColumn(name = "card_translation_id")
	private CardTranslation cardTranslation;

	@Column
	private String sources;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public CardTranslation getCardTranslation() {
		return cardTranslation;
	}

	public void setCardTranslation(CardTranslation cardTranslation) {
		this.cardTranslation = cardTranslation;
	}

	public String getSources() {
		return sources;
	}

	public void setSources(String sources) {
		this.sources = sources;
	}


}
