package com.pcagrade.retriever.card.pokemon.feature.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.pokemon.feature.FeatureDTO;
import com.pcagrade.retriever.card.pokemon.feature.FeatureService;
import com.pcagrade.retriever.card.pokemon.feature.translation.pattern.FeatureTranslationPatternDTO;
import com.pcagrade.retriever.card.pokemon.feature.translation.pattern.FeatureTranslationPatternService;
import com.pcagrade.retriever.card.pokemon.web.PokemonCardRestController;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/" + FeatureRestController.BASE_PATH)
public class FeatureRestController {

    public static final String BASE_PATH = PokemonCardRestController.BASE_PATH + "/features";
    @Autowired
	private FeatureService featureService;

	@Autowired
	private FeatureTranslationPatternService featureTranslationPatternService;

	@GetMapping
	public List<FeatureDTO> getFeatures() {
		return featureService.getFeatures();
	}

	@GetMapping("/{id}")
	public Optional<FeatureDTO> getFeature(@PathVariable("id") Ulid id) {
		return featureService.findById(id);
	}

	@PutMapping
	public void saveFeature(@RequestBody FeatureDTO feature) {
		featureService.saveFeature(feature);
	}

	@PostMapping
	public Ulid addFeature(@RequestBody FeatureDTO feature) {
		return featureService.saveFeature(feature);
	}

	@GetMapping("/{id}/patterns")
	public List<FeatureTranslationPatternDTO> getFeaturePatterns(@PathVariable("id") Ulid id) {
		return featureTranslationPatternService.getByFeatureId(id);
	}

	@PutMapping("/{id}/patterns")
	public void setFeaturePatterns(@PathVariable("id") Ulid id, @RequestBody List<FeatureTranslationPatternDTO> patterns) {
		featureTranslationPatternService.setPatterns(id, patterns);
	}

}
