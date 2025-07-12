package com.pcagrade.retriever.card.onepiece.source.official;

import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.onepiece.OnePieceFieldMappingService;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.parser.IHTMLParser;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import jakarta.annotation.Nonnull;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Component
@CacheConfig(cacheNames = "onePieceOfficialSiteParser")
public class OnePieceOfficialSiteParser {

    private static final Pattern PARRAREL_PATTERN = Pattern.compile("_p(\\d+)$");

    @Autowired
    private IHTMLParser htmlParser;
    @Autowired
    private OnePieceFieldMappingService onePieceFieldMappingService;

    @Value("${onepiece-cardgame-com.url.us}")
    private String onepieceCardgameComUrlUs;
    @Value("${onepiece-cardgame-com.url.jp}")
    private String onepieceCardgameComUrlJp;

    @Cacheable
    public List<OnePieceOfficialSiteSet> getSets() {
        return ListUtils.union(getSets(Localization.USA), getSets(Localization.JAPAN));
    }

    private List<OnePieceOfficialSiteSet> getSets(Localization localization) {
        try {
           return get("cardlist/", localization).select("select#series option").stream()
                    .map(e -> createSet(e, localization))
                   .filter(set -> set.id() != 0)
                    .toList();
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    @Nonnull
    private OnePieceOfficialSiteSet createSet(Element option, Localization localization) {
        var id = getSetId(option);
        var text = option.text();
        var code = onePieceFieldMappingService.mapCode(localization, PCAUtils.substringBetween(text, "[", "]"));
        var serie = PCAUtils.clean(StringUtils.substringBefore(text, "<br"));
        var name = PCAUtils.clean(StringUtils.substringBetween(text, ">", "["));

        if (StringUtils.isBlank(name)) {
            name = serie;
            serie = "";
        }
        if (StringUtils.containsIgnoreCase(name, "promo")) {
            serie = "Promo";
            code = "P";
        } else if (StringUtils.containsIgnoreCase(name, "Limited Product Card")) {
            serie = "Promo";
            code = "LPC";
        }

        return new OnePieceOfficialSiteSet(id, localization, WordUtils.capitalizeFully(name), code, WordUtils.capitalizeFully(serie));
    }

    private static int getSetId(Element option) {
        try {
            return Integer.parseInt(option.attr("value"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public List<OnePieceOfficialSiteCard> getCards(Localization localization, int setId, boolean promo) {
        try {
            return get("cardlist/?series=" + setId, localization).select("dl.modalCol").stream()
                    .map(dl -> createCard(dl, localization, promo))
                    .toList();
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    @Nonnull
    private OnePieceOfficialSiteCard createCard(Element dl, Localization localization, boolean promo) {
        var id = dl.id();
        var infoCol = dl.selectFirst("div.infoCol").text().split("\\|");

        var name = dl.selectFirst("div.cardName").text();
        var number = PCAUtils.clean(infoCol[0]);
        var rarity =  PCAUtils.clean(infoCol[1]);
        var type =  PCAUtils.clean(infoCol[2]);
        var parallel = getParallel(id);
        var attribute = localization == Localization.JAPAN ? "" : PCAUtils.clean(dl.selectFirst("div.attribute>i").text());
        var color =  PCAUtils.clean(RegExUtils.removeAll(dl.selectFirst("div.color").text(), Pattern.compile("color", Pattern.CASE_INSENSITIVE)));
        List<String> distributions = promo ? dl.select("div.getInfo").stream()
                .flatMap(e -> e.childNodes().stream())
                .mapMulti(PCAUtils.cast(TextNode.class))
                .map(e -> PCAUtils.clean(e.text()))
                .filter(StringUtils::isNotBlank)
                .toList() : Collections.emptyList();

        return new OnePieceOfficialSiteCard(PCAUtils.clean(StringUtils.removeEnd(name, " (Parallel)")), localization, number, type, rarity, parallel, attribute, color, distributions);
    }

    private static int getParallel(String id) {
        try {
            var matcher = PARRAREL_PATTERN.matcher(id);

            if (!matcher.find()) {
                return 0;
            }
            return Integer.parseInt(matcher.group(1));
        } catch (Exception e) {
            return 0;
        }
    }

    @Nonnull
    private Element get(String url, Localization localization) throws IOException {
        return htmlParser.get(htmlParser.processUrl(localization == Localization.JAPAN ? this.onepieceCardgameComUrlJp : this.onepieceCardgameComUrlUs, url)).body();
    }
}
