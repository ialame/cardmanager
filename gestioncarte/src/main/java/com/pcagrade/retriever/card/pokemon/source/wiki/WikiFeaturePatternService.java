package com.pcagrade.retriever.card.pokemon.source.wiki;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WikiFeaturePatternService {

    @Autowired
    private List<PokemonWikiTranslatorFactory> pokemonWikiTranslatorFactories;

    @Autowired
    private PokemonSetService pokemonSetService;

    public List<String> getNames() {
        return pokemonWikiTranslatorFactories.stream()
                .map(t -> t.getParser().getName())
                .toList();
    }

    public List<WikiFeaturePattern> searchPatterns(String wiki, Ulid setId) {
        return pokemonSetService.findSet(setId).stream()
                .<WikiFeaturePattern>mapMulti((set, downstream) -> pokemonWikiTranslatorFactories.stream()
                        .filter(t -> t.getParser().getName().equals(wiki))
                        .forEach(t -> t.searchPatterns(set).forEach(downstream)))
                .toList();
    }
}
