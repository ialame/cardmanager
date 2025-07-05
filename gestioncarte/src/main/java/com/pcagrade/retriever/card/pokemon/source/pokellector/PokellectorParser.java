package com.pcagrade.retriever.card.pokemon.source.pokellector;

import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.image.ExtractedImageDTO;
import com.pcagrade.retriever.parser.IHTMLParser;
import jakarta.annotation.Nonnull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class PokellectorParser {

    @Autowired
    private IHTMLParser htmlParser;

    @Value("${pokellector-com.url}")
    private String pokellectorUrl;

    private static final Pattern NUMBER_PATTERN = Pattern.compile("#(\\d+)");

    public Map<String, ExtractedImageDTO> getImages(String setName, Localization localization) {
        return get(getUrl(setName))
                .flatMapIterable(b -> b.select("div.card"))
                .flatMap(e -> {
                    var name = e.select("div.plaque").text();
                    var image = e.select("img").attr("data-src");
                    var match = NUMBER_PATTERN.matcher(name);
                    var number = match.find() ? match.group(1) : name;

                    return getImage(image.replace(".thumb", ""), localization)
                            .map(i -> Map.entry(number, i));
                })
                .collect(PCAUtils.toMap())
                .blockOptional()
                .orElse(Collections.emptyMap());
    }

    private Mono<ExtractedImageDTO> getImage(String url, Localization localization) {
        return Mono.just(new ExtractedImageDTO(localization, "pokellector", url, false, null));
    }

    public byte[] getImage(String url) {
        return htmlParser.getImage(url).block();
    }


    public String getUrl(String setName) {
        return pokellectorUrl + setName;
    }

    @Nonnull
    private Mono<Element> get(String url) {
        return htmlParser.getMono(htmlParser.processUrl(this.pokellectorUrl, url))
                .map(Document::body);
    }
}
