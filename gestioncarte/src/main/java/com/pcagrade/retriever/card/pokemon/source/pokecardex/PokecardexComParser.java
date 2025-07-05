package com.pcagrade.retriever.card.pokemon.source.pokecardex;

import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.parser.IHTMLParser;
import jakarta.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@CacheConfig(cacheNames = "pokecardexComParser")
public class PokecardexComParser {

    @Autowired
    private IHTMLParser htmlParser;

    @Value("${pokecardex-com.url}")
    private String pokecardexUrl;

    @Cacheable
    public Map<String, String> parse(String code) {
        var anchorsNames = parseAnchors(code);
        var divNames = parseNameDivs(code);
        var result = new HashMap<>(anchorsNames);

        divNames.forEach((key, value) -> result.merge(key, value, (v1, v2) -> v1));
        return result;
    }

    public Map<String, String> parseAnchors(String code) {
        return listElements(code, "div.serie-details-carte>a").stream()
                .map(a -> a.attr("title"))
                .filter(StringUtils::isNotBlank)
                .map(PokecardexComParser::createPair)
                .filter(p -> StringUtils.isNotBlank(p.getLeft()) && StringUtils.isNotBlank(p.getRight()))
                .collect(PCAUtils.toMap());
    }

    public Map<String, String> parseNameDivs(String code) {
        return listElements(code, "div.serie-details-nom-carte").stream()
                .map(Element::text)
                .filter(StringUtils::isNotBlank)
                .map(PokecardexComParser::createPair)
                .filter(p -> StringUtils.isNotBlank(p.getLeft()) && StringUtils.isNotBlank(p.getRight()))
                .collect(PCAUtils.toMap());
    }

    @Nonnull
    private static Pair<String, String> createPair(String name) {
        var index = name.lastIndexOf(" ");

        return Pair.of(name.substring(index + 1), PCAUtils.clean(name.substring(0, index)));
    }

    private List<Element> listElements(String code, String selector) {
        try {
            var body = get("series/" + code);

            var value = body.select(selector);

            if (CollectionUtils.isEmpty(value)) {
                return Collections.emptyList();
            }
            return value;
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public String getUrl(String code) {
        return pokecardexUrl + "series/"  + code;
    }

    @Nonnull
    private Element get(String url) throws IOException {
        return htmlParser.get(htmlParser.processUrl(this.pokecardexUrl, url)).body();
    }


}
