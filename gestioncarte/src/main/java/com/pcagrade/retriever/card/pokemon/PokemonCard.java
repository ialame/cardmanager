package com.pcagrade.retriever.card.pokemon;

import com.pcagrade.mason.ulid.jpa.UlidColumnDefinitions;
import com.pcagrade.retriever.card.Card;
import com.pcagrade.retriever.card.pokemon.bracket.Bracket;
import com.pcagrade.retriever.card.pokemon.feature.Feature;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "pokemon_card")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("pok")
@Audited
public class PokemonCard extends Card {

	@Column(name = "Card")
	private String card;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH })
	@JoinTable(name = "cartepokemons_crochets",
			joinColumns = @JoinColumn(name = "pokemon_card_id"),
			inverseJoinColumns = @JoinColumn(name = "crochet_id"))
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private Set<Bracket> brackets = new HashSet<>();

	@ManyToMany(cascade = CascadeType.MERGE)
	@JoinTable(name = "cartepokemons_particularites",
			joinColumns = @JoinColumn(name = "pokemon_card_id"),
			inverseJoinColumns = @JoinColumn(name = "pokemon_card_feature_id"))
	@Audited
	private Set<Feature> features = new HashSet<>();

	@Column(name = "Type1")
	private String type;

	@Column(name = "Type2")
	private String type2;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "carte_mere_id", columnDefinition = UlidColumnDefinitions.DEFAULT_NULL)
	private PokemonCard parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = CascadeType.ALL)
	private List<PokemonCard> children = new ArrayList<>();

	@Column(name = "FA")
	private Boolean fullArt = false;

	@Column(name = "id_prim")
	private String idPrim;

	@Column(name = "id_prim_jap")
	private String idPrimJp;

	@Column(name = "LV")
	private Integer level;

	@Column(name = "has_img")
	private Boolean hasImage = false;

	@Column(name = "is_distribution_fille")
	private Boolean distribution = false;

	@Column(name = "is_crochet_fille")
	private Boolean childWithBracket = false;

	@Column
	private int status = 0;


	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}

	public Set<Bracket> getBrackets() {
		return brackets;
	}

	public void setBrackets(Set<Bracket> brackets) {
		this.brackets = brackets;
	}

	public Set<Feature> getFeatures() {
		return features;
	}

	public void setFeatures(Set<Feature> features) {
		this.features = features;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public PokemonCard getParent() {
		return parent;
	}

	public void setParent(PokemonCard parent) {
		this.parent = parent;
	}

	public List<PokemonCard> getChildren() {
		return children;
	}

	public String getIdPrim() {
		return idPrim;
	}

	public void setIdPrim(String idPrim) {
		this.idPrim = idPrim;
	}

	public String getIdPrimJp() {
		return idPrimJp;
	}

	public void setIdPrimJp(String idPrimJp) {
		this.idPrimJp = idPrimJp;
	}

	public Boolean isDistribution() {
		return distribution;
	}

	public void setDistribution(Boolean distribution) {
		this.distribution = distribution;
	}

	public Boolean isFullArt() {
		return fullArt;
	}

	public void setFullArt(Boolean fullArt) {
		this.fullArt = fullArt;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Boolean getChildWithBracket() {
		return childWithBracket;
	}

	public void setChildWithBracket(Boolean childWithBracket) {
		this.childWithBracket = childWithBracket;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getType2() {
		return type2;
	}

	public void setType2(String type2) {
		this.type2 = type2;
	}
}
