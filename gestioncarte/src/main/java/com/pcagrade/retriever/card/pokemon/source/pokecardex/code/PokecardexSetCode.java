package com.pcagrade.retriever.card.pokemon.source.pokecardex.code;

import com.pcagrade.retriever.card.pokemon.set.PokemonSet;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "j_pokecardex_set_code")
public class PokecardexSetCode {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToOne
	@JoinColumn(name = "set_id")
	private PokemonSet set;

	@Column(name = "code")
	private String code;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public PokemonSet getSet() {
		return set;
	}

	public void setSet(PokemonSet set) {
		this.set = set;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
