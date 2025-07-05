package com.pcagrade.retriever.card.pokemon.set;

import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.expansion.ExpansionBulbapedia;
import com.pcagrade.retriever.card.pokemon.source.official.jp.source.JapaneseOfficialSiteSource;
import com.pcagrade.retriever.card.pokemon.source.official.path.OfficialSiteSetPath;
import com.pcagrade.retriever.card.pokemon.source.pkmncards.path.PkmncardsComSetPath;
import com.pcagrade.retriever.card.pokemon.source.ptcgo.PtcgoSet;
import com.pcagrade.retriever.card.pokemon.source.wiki.url.WikiUrl;
import com.pcagrade.retriever.card.set.CardSet;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "pokemon_set")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("pok")
public class PokemonSet extends CardSet {

	@Column(name = "num_sur")
	private String printedTotal;

	@Column(name = "nb_cartes")
	private String totalNumber;

	@Column(name = "is_promo")
	private Boolean promo;

	@Column(name = "nom_raccourci")
	private String shortName;

	@Column(name = "id_pca")
	private Integer idPca;

	@OneToMany(mappedBy = "set")
	private List<PtcgoSet> ptcgoSets;

	@OneToMany(mappedBy = "set", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@MapKey(name = "localization")
	private Map<Localization, WikiUrl> wikiUrls = new EnumMap<>(Localization.class);

	@OneToMany(mappedBy = "set", cascade = CascadeType.ALL)
	private List<OfficialSiteSetPath> officialSitePaths;

	@OneToMany(mappedBy = "set", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<PkmncardsComSetPath> pkmncardsComPath;

	@OneToOne(mappedBy = "set", cascade = CascadeType.ALL)
	private JapaneseOfficialSiteSource japaneseOfficialSiteSource;

	@OneToMany(mappedBy = "set", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<ExpansionBulbapedia> expansionsBulbapedia = new ArrayList<>();

	public String getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(String totalNumber) {
		this.totalNumber = totalNumber;
	}

	public boolean isPromo() {
		return Boolean.TRUE.equals(promo);
	}

	public void setPromo(Boolean promo) {
		this.promo = promo;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public List<PtcgoSet> getPtcgoSets() {
		return ptcgoSets;
	}

	public void setPtcgoSets(List<PtcgoSet> ptcgoSets) {
		this.ptcgoSets = ptcgoSets;
	}

	public List<OfficialSiteSetPath> getOfficialSitePaths() {
		return officialSitePaths;
	}

	public void setOfficialSitePath(List<OfficialSiteSetPath> officialSitePaths) {
		this.officialSitePaths = officialSitePaths;
	}

	public JapaneseOfficialSiteSource getJapaneseOfficialSiteSource() {
		return japaneseOfficialSiteSource;
	}

	public void setJapaneseOfficialSiteSource(JapaneseOfficialSiteSource japaneseOfficialSiteSource) {
		this.japaneseOfficialSiteSource = japaneseOfficialSiteSource;
	}

	public Integer getIdPca() {
		return idPca;
	}

	public void setIdPca(Integer idPca) {
		this.idPca = idPca;
	}

	public String getPrintedTotal() {
		return printedTotal;
	}

	public void setPrintedTotal(String printedTotal) {
		this.printedTotal = printedTotal;
	}
}
