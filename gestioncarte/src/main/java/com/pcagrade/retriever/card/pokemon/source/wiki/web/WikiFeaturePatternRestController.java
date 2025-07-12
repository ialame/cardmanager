package com.pcagrade.retriever.card.pokemon.source.wiki.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.pokemon.feature.web.FeatureRestController;
import com.pcagrade.retriever.card.pokemon.source.wiki.WikiFeaturePattern;
import com.pcagrade.retriever.card.pokemon.source.wiki.WikiFeaturePatternService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/" + FeatureRestController.BASE_PATH + "/wikis")
public class WikiFeaturePatternRestController {

    @Autowired
    private WikiFeaturePatternService wikiFeaturePatternService;

    @GetMapping("/names")
    public List<String> getNames() {
        return wikiFeaturePatternService.getNames();
    }

    @GetMapping("/patterns/{name}/{setId}")
    public List<WikiFeaturePattern> searchPatterns(@PathVariable("name") String name, @PathVariable("setId") Ulid setId) {
        return wikiFeaturePatternService.searchPatterns(name, setId);
    }
}
