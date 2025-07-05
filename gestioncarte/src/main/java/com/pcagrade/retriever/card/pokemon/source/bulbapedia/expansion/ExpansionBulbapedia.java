package com.pcagrade.retriever.card.pokemon.source.bulbapedia.expansion;

import com.pcagrade.retriever.card.pokemon.set.PokemonSet;
import com.pcagrade.mason.localization.Localization;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "j_expansion_bulbapedia")
public class ExpansionBulbapedia {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "charset", nullable = false)
	private String charset;

	@Column(name = "nom")
	private String name;

	@Column(name = "page_2_name")
	private String page2Name;

	@Column(name = "nomH3")
	private String h3Name;

	@Column(name = "premier_numero")
	private String firstNumber;

	@Column(name = "nom_tableau")
	private String tableName;

	private String url;
	
	@ManyToOne
	@JoinColumn(name = "set_id", referencedColumnName = "id")
	private PokemonSet set;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFirstNumber() {
		return firstNumber;
	}

	public void setFirstNumber(String firstNumber) {
		this.firstNumber = firstNumber;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Transient
	public Localization getLocalization() {
		return "jp".equalsIgnoreCase(charset) ? Localization.JAPAN : Localization.USA;
	}
	
	@Transient
	public void setLocalization(Localization localization) {
		charset = localization.getCode();
	}
	
	public PokemonSet getSet() {
		return set;
	}

	public void setSet(PokemonSet set) {
		this.set = set;
	}

	public String getPage2Name() {
		return page2Name;
	}

	public void setPage2Name(String page2Name) {
		this.page2Name = page2Name;
	}

	public String getH3Name() {
		return h3Name;
	}

	public void setH3Name(String h3Name) {
		this.h3Name = h3Name;
	}
}
