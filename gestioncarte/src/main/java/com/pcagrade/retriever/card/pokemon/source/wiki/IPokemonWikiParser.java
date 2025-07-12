package com.pcagrade.retriever.card.pokemon.source.wiki;

import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.source.wiki.url.WikiUrlDTO;
import com.pcagrade.mason.localization.Localization;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public interface IPokemonWikiParser {

    String getName();
    boolean canTranslateTo(Localization localization);
    Map<String, Pair<String, String>> parse(PokemonSetDTO set);
    List<Pair<String, String>> searchPatterns(PokemonSetDTO set);
    String getUrl(PokemonSetDTO set, @Nonnull List<WikiUrlDTO> wikiUrls);
    String getUrl(PokemonSetDTO set);
}
