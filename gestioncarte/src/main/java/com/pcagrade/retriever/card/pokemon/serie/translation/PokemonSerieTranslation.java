package com.pcagrade.retriever.card.pokemon.serie.translation;

import com.pcagrade.retriever.serie.SerieTranslation;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "pokemon_serie_translation")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("pok")
public class PokemonSerieTranslation extends SerieTranslation {

	@Column
	private String code;


	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
