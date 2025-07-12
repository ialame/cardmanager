package com.pcagrade.retriever.card.pokemon.set.web;

import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.expansion.ExpansionBulbapediaDTO;
import com.pcagrade.retriever.card.pokemon.source.ptcgo.PtcgoSetDTO;
import com.pcagrade.retriever.card.pokemon.source.wiki.url.WikiUrlDTO;
import com.pcagrade.mason.localization.Localization;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class EditSetForm {

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private PokemonSetDTO set;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private List<ExpansionBulbapediaDTO> expansionsBulbapedia;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private List<PtcgoSetDTO> ptcgoSets;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private Map<Localization, List<WikiUrlDTO>> wikiUrls;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private List<String> officialSitePath;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private String pkmncardsComSetPath;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private List<String> japaneseOfficialSitePgs;
	private String pokecardexCode;


	public EditSetForm() {
		set = new PokemonSetDTO();
		expansionsBulbapedia =  new ArrayList<>();
		ptcgoSets = new ArrayList<>();
		wikiUrls = new EnumMap<>(Localization.class);
	}

	public PokemonSetDTO getSet() {
		return set;
	}

	public void setSet(PokemonSetDTO set) {
		this.set = set;
	}

	public List<PtcgoSetDTO> getPtcgoSets() {
		return ptcgoSets;
	}

	public void setPtcgoSets(List<PtcgoSetDTO> ptcgoSets) {
		this.ptcgoSets = ptcgoSets;
	}

	public Map<Localization, List<WikiUrlDTO>> getWikiUrls() {
		return wikiUrls;
	}

	public void setWikiUrls(Map<Localization, List<WikiUrlDTO>> wikiUrls) {
		this.wikiUrls = wikiUrls;
	}

	public List<String> getOfficialSitePath() {
		return officialSitePath;
	}

	public void setOfficialSitePath(List<String> officialSitePath) {
		this.officialSitePath = officialSitePath;
	}

	public List<ExpansionBulbapediaDTO> getExpansionsBulbapedia() {
		return expansionsBulbapedia;
	}

	public void setExpansionsBulbapedia(List<ExpansionBulbapediaDTO> expansionsBulbapedia) {
		this.expansionsBulbapedia = expansionsBulbapedia;
	}

	public List<String> getJapaneseOfficialSitePgs() {
		return japaneseOfficialSitePgs;
	}

	public void setJapaneseOfficialSitePgs(List<String> japaneseOfficialSitePgs) {
		this.japaneseOfficialSitePgs = japaneseOfficialSitePgs;
	}

	public String getPkmncardsComSetPath() {
		return pkmncardsComSetPath;
	}

	public void setPkmncardsComSetPath(String pkmncardsComSetPath) {
		this.pkmncardsComSetPath = pkmncardsComSetPath;
	}

	public String getPokecardexCode() {
		return pokecardexCode;
	}

	public void setPokecardexCode(String pokecardexCode) {
		this.pokecardexCode = pokecardexCode;
	}
}
