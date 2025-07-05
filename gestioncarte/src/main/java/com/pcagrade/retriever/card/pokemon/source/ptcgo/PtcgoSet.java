package com.pcagrade.retriever.card.pokemon.source.ptcgo;

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
@Table(name = "j_ptcgo_set")
public class PtcgoSet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "set_id")
	private PokemonSet set;
	
	@Column(name = "file_name")
	private String fileName;
	
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
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
