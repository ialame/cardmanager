package com.pcagrade.retriever.card.pokemon.source.pkmncards;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.source.pkmncards.path.PkmncardsComSetPathService;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.parser.IHTMLParser;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.regex.Pattern;

@Component
public class PkmncardsComParser {

    @Autowired
    private IHTMLParser htmlParser;

    @Autowired
    private PkmncardsComSetPathService pkmncardsComSetPathService;

    @Value("${pkmncards-com.url}")
    private String pkmncardsComURL;

    public String getCardArtist(PokemonSetDTO set, PokemonCardDTO card) {
        return getCardArtist(set.getId(), card);
    }

    public String getCardArtist(Ulid setId, PokemonCardDTO card) {
        return PCAUtils.clean(getField(setId, card, "div.illus>span>a"));
    }

    public String getName(PokemonSetDTO set, PokemonCardDTO card) {
        return getName(set.getId(), card);
    }

    public String getName(Ulid setId, PokemonCardDTO card) {
        return PCAUtils.clean(getField(setId, card, "span.name>a"));
    }
    
    public String getRarity(PokemonSetDTO set, PokemonCardDTO card) {
        return getRarity(set.getId(), card);
    }

    public String getRarity(Ulid setId, PokemonCardDTO card) {
        return PCAUtils.clean(getField(setId, card, "span.rarity>a"));
    }

    private String getField(Ulid setId, PokemonCardDTO card, String selector) {
        try {
            var link = getCardLink(setId, card);
            if (StringUtils.isBlank(link)) {
                return "";
            }

            var element = get(link).select(selector).first();

            if (element == null) {
                return "";
            }
            return element.text();
        }
        catch (IOException e) {
            return "";
        }
    }

    @Nonnull
    public String getCardLink(Ulid setId, PokemonCardDTO card) throws IOException {
        var cardTranslation = card.getTranslations().get(Localization.USA);

        if (cardTranslation == null) {
            return "";
        }

        var url = getSetUrl(setId);

        if (StringUtils.isBlank(url)) {
            return "";
        }

        var number = cardTranslation.getNumber();
        var split = number.split("/");
        var pattern = Pattern.compile("#" + (split.length > 1 ? split[0] : number) + "(\\D|$)", Pattern.CASE_INSENSITIVE);

        return get(url).select("a").stream()
                .filter(element -> pattern.matcher(element.attr("title")).find())
                .map(e -> e.attr("href"))
                .filter(StringUtils::isNotBlank)
                .findFirst()
                .orElse("");
    }

    @Nonnull
    private String getSetUrl(Ulid setId) {
        return pkmncardsComSetPathService.getPath(setId);
    }

    @Nonnull
    private Element get(String url) throws IOException {
        return htmlParser.get(htmlParser.processUrl(this.pkmncardsComURL, url)).body();
    }
}
