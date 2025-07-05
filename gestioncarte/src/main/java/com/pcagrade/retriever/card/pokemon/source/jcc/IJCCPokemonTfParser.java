package com.pcagrade.retriever.card.pokemon.source.jcc;

import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.cache.annotation.Cacheable;

import java.util.Map;

public interface IJCCPokemonTfParser {
    @Cacheable
    Map<String, Pair<String, String>> parse(PokemonSetDTO set);

    String getUrl(PokemonSetDTO set);
}
