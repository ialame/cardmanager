package com.pcagrade.retriever.card.pokemon.set.translation;

import com.pcagrade.retriever.card.set.CardSetTranslation;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "pokemon_set_translation")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("pok")
public class PokemonSetTranslation extends CardSetTranslation {



	@Column(name = "original_name")
	private String originalName = "";

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}
}
