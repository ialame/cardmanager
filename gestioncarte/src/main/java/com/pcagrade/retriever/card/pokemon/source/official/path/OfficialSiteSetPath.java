package com.pcagrade.retriever.card.pokemon.source.official.path;

import com.pcagrade.retriever.card.pokemon.set.PokemonSet;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "j_official_site_set_path")
public class OfficialSiteSetPath {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "set_id")
	private PokemonSet set;

	@Column(name = "path")
	private String path;

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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
