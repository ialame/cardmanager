package com.pcagrade.retriever.card.pokemon.source.bulbapedia.web;

import com.pcagrade.retriever.card.pokemon.source.bulbapedia.expansion.ExpansionBulbapediaDTO;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.expansion.ExpansionBulbapediaService;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction.BulbapediaExtractionService;
import com.pcagrade.retriever.card.pokemon.web.PokemonCardRestController;
import com.pcagrade.retriever.image.ExtractedImageDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/" + BulbapediaRestController.BASE_PATH)
public class BulbapediaRestController {

	public static final String BASE_PATH = PokemonCardRestController.BASE_PATH + "/bulbapedia";

	@Autowired
	private ExpansionBulbapediaService expansionBulbapediaService;

	@Autowired
	private BulbapediaExtractionService bulbapediaExtractionService;

	@GetMapping("/expansions")
	public List<ExpansionBulbapediaDTO> bulbapediaPage(@RequestParam(value = "url", required = false) String url) {
		if (StringUtils.isNotBlank(url)) {
			return expansionBulbapediaService.findByUrl(url);
		}
		return expansionBulbapediaService.getExpansions();
	}

	@PostMapping("/sets/icons/search")
	public List<ExtractedImageDTO> searchSetIcon(@RequestBody List<ExpansionBulbapediaDTO> expansions) {
		return bulbapediaExtractionService.findImagesForExpansions(expansions);
	}

	@PostMapping("/images/search")
	public List<ExtractedImageDTO> searchCardImage(@RequestBody List<ExpansionBulbapediaDTO> expansions) {
		return bulbapediaExtractionService.findImagesForExpansions(expansions);
	}
}
