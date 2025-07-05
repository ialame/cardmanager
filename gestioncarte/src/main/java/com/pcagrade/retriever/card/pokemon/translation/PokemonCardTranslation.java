package com.pcagrade.retriever.card.pokemon.translation;

import com.pcagrade.retriever.card.Card;
import com.pcagrade.retriever.card.CardTranslation;
import com.pcagrade.retriever.card.pokemon.PokemonCard;
import org.hibernate.envers.Audited;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "pokemon_card_translation")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("pok")
@Audited
public class PokemonCardTranslation extends CardTranslation {


	@Column(name = "num")
	private String number;

	@Column(name = "original_name")
	private String originalName = "";

	@Column
	private String rarity;

	@Column
	private String trainer = "";

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@Transient
	@Nullable
	public PokemonCard getCard() {
		Card card = this.getTranslatable();

		return card instanceof PokemonCard pokemonCard ? pokemonCard : null;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public String getRarity() {
		return rarity;
	}

	public void setRarity(String rarity) {
		this.rarity = rarity;
	}

	public String getTrainer() {
		return trainer;
	}

	public void setTrainer(String trainer) {
		this.trainer = trainer;
	}
}
