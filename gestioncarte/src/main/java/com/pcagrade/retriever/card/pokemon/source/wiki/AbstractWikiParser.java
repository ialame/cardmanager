package com.pcagrade.retriever.card.pokemon.source.wiki;

import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.source.wiki.url.WikiUrlDTO;
import com.pcagrade.retriever.card.pokemon.source.wiki.url.WikiUrlService;
import com.pcagrade.retriever.card.pokemon.translation.GenericNameParser;
import com.pcagrade.mason.localization.Localization;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public abstract class AbstractWikiParser implements IPokemonWikiParser {

    @Autowired
    private GenericNameParser genericNameParser;

    @Autowired
    private WikiUrlService wikiUrlService;

    private final String name;
    private final Localization localization;

    protected AbstractWikiParser(String name, Localization localization) {
        this.name = name;
        this.localization = localization;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean canTranslateTo(Localization localization) {
        return localization == this.localization;
    }

    @Override
    public String getUrl(PokemonSetDTO set) {
        return getUrl(set, getWikiUrl(set));
    }

    @Override
    @Cacheable(cacheResolver = "runtimeCacheResolver")
    public Map<String, Pair<String, String>> parse(PokemonSetDTO set) {
        return streamCardLines(set).<Pair<String, Pair<String, String>>>mapMulti((tr, downstream) -> {
            var children = tr.children();
            var pair = genericNameParser.parseName(children.get(1), name, localization);

            if (pair != null) {
                downstream.accept(Pair.of(children.get(0).text(), pair));
            }
        }).collect(PCAUtils.toMap());
    }

    @Override
    public List<Pair<String, String>> searchPatterns(PokemonSetDTO set) {
        return streamCardLines(set)
                .<Pair<String, String>>mapMulti((tr, downstream) -> genericNameParser.searchPatterns(tr.children().get(1)).forEach(downstream))
                .distinct()
                .toList();
    }

    private Stream<Element> streamCardLines(PokemonSetDTO set) {
        var wikiUrls = getWikiUrl(set);
        var url = getUrl(set, wikiUrls);

        if (StringUtils.isBlank(url)) {
            return Stream.empty();
        }
        return listTrs(url, wikiUrls).stream()
                .filter(tr -> {
                    var children = tr.children();

                    return children.size() >= 2 && children.get(0).tagName().equalsIgnoreCase("td");
                });
    }

    @Nonnull
    private List<WikiUrlDTO> getWikiUrl(PokemonSetDTO set) {
        return wikiUrlService.getUrls(set.getId(), localization);
    }

    protected abstract List<Element> listTrs(String url, @Nonnull List<WikiUrlDTO> wikiUrls);
}
