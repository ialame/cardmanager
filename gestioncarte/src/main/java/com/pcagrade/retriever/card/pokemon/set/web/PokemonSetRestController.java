package com.pcagrade.retriever.card.pokemon.set.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.card.pokemon.IPokemonCardService;
import com.pcagrade.retriever.card.pokemon.serie.PokemonSerieService;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetService;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.expansion.ExpansionBulbapediaDTO;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.expansion.ExpansionBulbapediaService;
import com.pcagrade.retriever.card.pokemon.source.official.jp.source.JapaneseOfficialSiteSourceService;
import com.pcagrade.retriever.card.pokemon.source.official.path.OfficialSiteSetPathService;
import com.pcagrade.retriever.card.pokemon.source.pkmncards.path.PkmncardsComSetPathService;
import com.pcagrade.retriever.card.pokemon.source.pokecardex.code.PokecardexSetCodeService;
import com.pcagrade.retriever.card.pokemon.source.ptcgo.PtcgoService;
import com.pcagrade.retriever.card.pokemon.source.wiki.url.WikiUrlService;
import com.pcagrade.retriever.card.pokemon.web.PokemonCardRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/" + PokemonSetRestController.BASE_PATH)
public class PokemonSetRestController {

    public static final String BASE_PATH = PokemonCardRestController.BASE_PATH + "/sets";
    @Autowired
	IPokemonCardService pokemonCardService;

	@Autowired
	PokemonSetService pokemonSetService;

	@Autowired
	PokemonSerieService pokemonSerieService;

	@Autowired
	ExpansionBulbapediaService expansionBulbapediaService;

	@Autowired
	PtcgoService ptcgoService;

	@Autowired
	private WikiUrlService wikiUrlService;

	@Autowired
	private OfficialSiteSetPathService officialSiteSetPathService;

	@Autowired
	private PkmncardsComSetPathService pkmncardsComSetPathService;

	@Autowired
	private JapaneseOfficialSiteSourceService japaneseOfficialSiteSourceService;

	@Autowired
	private PokecardexSetCodeService pokecardexSetCodeService;

	@GetMapping
	public List<PokemonSetDTO> getSets() {
		return pokemonSetService.getSets();
	}

	@GetMapping("/{id}/form")
	public EditSetForm getSetForm(@PathVariable Ulid id) {
		var setForm = new EditSetForm();
		var wikiUrls = setForm.getWikiUrls();


		pokemonSetService.findSet(id).ifPresent(set -> {
			setForm.setSet(set);
			setForm.setExpansionsBulbapedia(expansionBulbapediaService.findBySetId(set.getId()));
			setForm.setPtcgoSets(ptcgoService.findBySetId(id));

			for (var localization : Localization.Group.WEST.getLocalizations()) {
				wikiUrls.put(localization, wikiUrlService.getUrls(id, localization));
			}
			setForm.setOfficialSitePath(officialSiteSetPathService.getPaths(id));
			setForm.setPkmncardsComSetPath(pkmncardsComSetPathService.getPath(id));
			setForm.setJapaneseOfficialSitePgs(japaneseOfficialSiteSourceService.getPgs(id));
			setForm.setPokecardexCode(pokecardexSetCodeService.getCode(id));
		});
		return setForm;
	}

	@PutMapping
	public void saveSet(@RequestBody PokemonSetDTO set) {
		createSet(set);
	}

	@PostMapping
	public Ulid createSet(@RequestBody PokemonSetDTO set) {
		return pokemonSetService.save(set);
	}

	@PutMapping("/form")
	public void saveSet(@RequestBody EditSetForm setForm) {
		createSet(setForm);
	}

	@PostMapping("/form")
	public Ulid createSet(@RequestBody EditSetForm setForm) {
		var id = pokemonSetService.save(setForm.getSet());

		expansionBulbapediaService.setExpansions(id, setForm.getExpansionsBulbapedia());
		ptcgoService.setFilesForSet(id, setForm.getPtcgoSets());
		setForm.getWikiUrls().forEach((l, u) -> wikiUrlService.setUrls(id, l, u));
		officialSiteSetPathService.setPaths(id, setForm.getOfficialSitePath());
		pkmncardsComSetPathService.setPath(id, setForm.getPkmncardsComSetPath());
		japaneseOfficialSiteSourceService.setPgs(id, setForm.getJapaneseOfficialSitePgs());
		pokecardexSetCodeService.setCode(id, setForm.getPokecardexCode());
		return id;
	}

	@GetMapping("/{id}")
	public Optional<PokemonSetDTO> getSet(@PathVariable Ulid id) {
		return pokemonSetService.findSet(id);
	}

	@GetMapping("/short-name/{shortName}")
	public Optional<PokemonSetDTO> findByShortName(@PathVariable String shortName) {
		return pokemonSetService.findByShortName(shortName);
	}

	@DeleteMapping("/{id}")
	public void deleteSet(@PathVariable Ulid id) {
		pokemonCardService.deleteInSet(id);
		pokemonSetService.deleteSet(id);
	}

	@GetMapping("/{id}/bulbapedia")
	public List<ExpansionBulbapediaDTO> getBulbapediaExtension(@PathVariable Ulid id) {
		return expansionBulbapediaService.findBySetId(id);
	}

	@PostMapping("/{id}/rebuild-ids-prim")
	public void rebuildIdsPrim(@PathVariable Ulid id) {
		pokemonCardService.rebuildIdsPrim(id);
	}

	@PostMapping("/{id1}/{id2}/merge")
	public void mergeSets(@PathVariable Ulid id1, @PathVariable Ulid id2) {
		pokemonSetService.mergeSets(id1, id2);
	}

	@PutMapping("/{id}/image")
	public void setImage(@PathVariable Ulid id, @RequestBody String image) {
		pokemonSetService.setImage(id, image);
	}
}
