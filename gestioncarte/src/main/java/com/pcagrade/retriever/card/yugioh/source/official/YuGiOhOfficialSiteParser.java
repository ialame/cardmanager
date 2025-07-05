package com.pcagrade.retriever.card.yugioh.source.official;

import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.foil.Foil;
import com.pcagrade.retriever.card.yugioh.YuGiOhFieldMappingService;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.parser.IHTMLParser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;

import jakarta.annotation.Nonnull;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class YuGiOhOfficialSiteParser {

    private static final Logger LOGGER = LogManager.getLogger(YuGiOhOfficialSiteParser.class);

    private static final List<String> SNEAK_PEEKS = List.of(
            "Sneak Peek",
            "Sneak Peak",
            "Sneak Peeks",
            "Sneak Peekk",
            "Avant-première",
            "Presentación",
            "Core Booster Premiere!"
    );

    private static final List<String> SPECIAL_EDITIONS = List.of(
            "Special Edition",
            "Special Editions",
            "Edición Especial",
            "Édition Spéciale",
            "Edizione Speciale",
            "Edição Especial",
            "+1プラスワンボーナスパック",
            "+1ボーナスパック"
    );

    @Autowired
    private IHTMLParser htmlParser;
    @Autowired
    private YuGiOhFieldMappingService fieldMappingService;

    private final String dbYuGiOhComUrl;
    private final UriBuilderFactory uriBuilderFactory;

    public YuGiOhOfficialSiteParser(@Value("${db-yugioh-com.url}") String dbYuGiOhComUrl) {
        this.dbYuGiOhComUrl = dbYuGiOhComUrl;
        this.uriBuilderFactory = new DefaultUriBuilderFactory(dbYuGiOhComUrl);
    }

    @Cacheable("officialSiteSet")
    public List<OfficialSiteSet> getAllSets() {
        return Localization.Group.YUGIOH.stream()
                .<OfficialSiteSet>mapMulti((l, downstream) -> getAllSetsForLocalization(l).forEach(downstream))
                .toList();

    }

    private List<OfficialSiteSet> getAllSetsForLocalization(Localization localization) {
        try {
            var body = get("card_list.action", localization);

            return ListUtils.union(
                    parseSetsInTab(body.select("div#card_list_1"), localization, false),
                    parseSetsInTab(body.select("div#card_list_2"), localization, true)
            );
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    private List<OfficialSiteSet> parseSetsInTab(Elements tab, Localization localization, boolean promo) {
        var value = new ArrayList<OfficialSiteSet>();

        for (var typeSets : tab.select("div.pac_set")) {
            var type = typeSets.select("div.list_title>span").text();

            for (var series : typeSets.select("div.pack_m")) {
                var serie = series.select("p").text();
                var nextSibling = series.nextElementSibling();

                if (nextSibling == null) {
                    continue;
                }
                for (var set : nextSibling.select("div.pack")) {
                    var name = getSetName(set);
                    var url = set.select("input[type=hidden]").attr("value");
                    var pid = StringUtils.isNotBlank(url) ? PCAUtils.substringBetween(url, "pid=", "&") : "";

                    value.add(new OfficialSiteSet(localization, name, pid, type, serie, promo));
                }
            }
        }
        return value;
    }

    public List<OfficialSiteCard> getCards(String pid, Localization localization) {
        return doGetCards(pid, localization, 0);
    }

    @Nonnull
    public Optional<OfficialSiteCard> getFirstCard(String pid, Localization localization) {
        return doGetCards(pid, localization, 1).stream()
                .findFirst();
    }

    private List<OfficialSiteCard> doGetCards(String pid, Localization localization, long limit) {
        try {
            var setPage = getSetPage(pid, localization);
            var title = StringUtils.trimToEmpty(setPage.select("header#broad_title>div>h1").text());
            var sneakPeek = Localization.Group.WEST.contains(localization) && SNEAK_PEEKS.stream().anyMatch(s -> StringUtils.containsIgnoreCase(title, s));
            var count = new AtomicLong(0);

            return setPage.select("div#card_list>.t_row").stream()
                    .<OfficialSiteCard>mapMulti((c, downstream) -> {
                        if (limit > 0 && count.get() >= limit) {
                            return;
                        }

                        var name = c.select("span.card_name").text();
                        var rawTypes = PCAUtils.substringBetween(c.select("span.card_info_species_and_other_item").text(), "[", "]");
                        var types = Arrays.stream(StringUtils.split(rawTypes, "/"))
                                .map(String::trim)
                                .filter(StringUtils::isNotBlank)
                                .toList();
                        String cid = getCid(c);
                        var lines = getPage2SetLinesForPid(pid, cid, localization);

                        if (CollectionUtils.isEmpty(lines)) {
                            LOGGER.warn("No lines found for card {} ({} {})", name, localization.getCode(), cid);
                            if (limit == 0 || count.incrementAndGet() <= limit) {
                                downstream.accept(new OfficialSiteCard(cid, localization, "", name, OfficialSiteRarity.COMMON, null, sneakPeek, types));
                            }
                            return;
                        }
                        lines.stream()
                                .filter(l -> !l.rarity().isCommon() || lines.stream().noneMatch(l2 -> l2 != l && !l2.rarity().isCommon() && StringUtils.equals(l2.number(), l.number())))
                                .forEach(l -> {
                                    var number = StringUtils.trimToEmpty(l.number());

                                    LOGGER.debug("Found card {} ({})", name, number);
                                    if(limit == 0 || count.incrementAndGet() <= limit) {
                                        downstream.accept(new OfficialSiteCard(cid, localization, number, name, l.rarity(), l.releaseDate(), sneakPeek, types));
                                    }
                                });
                    }).toList();
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    @Nonnull
    private static String getCid(Element c) {
        var url = c.select("input[type=hidden].link_value").attr("value");

        return StringUtils.isNotBlank(url) ? PCAUtils.substringAfter(url, "cid=") : "";
    }

    @Nonnull
    private List<Page2SetLine> getPage2SetLinesForPid(String pid, String cid, Localization localization) {
        return getAllPage2SetLines(cid, localization).stream()
                .filter(c -> StringUtils.equalsIgnoreCase(c.setPid(), pid) && StringUtils.isNotBlank(c.number()))
                .toList();
    }

    @Nonnull
    private List<Page2SetLine> getAllPage2SetLines(String cid, Localization localization) {
        try {
            return getCardPage(cid, localization).select("div#update_list>div.t_body>div.t_row").stream()
                    .map(e -> createPage2SetLine(e, localization))
                    .toList();
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    @Nonnull
    private Page2SetLine createPage2SetLine(Element e, Localization localization) {
        var rarityDiv = e.select("div.icon>div");
        var rarity = StringUtils.trimToEmpty(rarityDiv.select("span").text());
        var rarityCode = StringUtils.trimToEmpty(rarityDiv.select("p").text());
        var number = StringUtils.trimToEmpty(e.select("div.card_number").text());
        var pid = PCAUtils.substringBetween(e.select("input.link_value").attr("value"), "pid=", "&");

        return new Page2SetLine(
                parseDate(StringUtils.trimToEmpty(e.select("div.time").text())),
                fieldMappingService.mapNumber(localization, number),
                pid,
                new OfficialSiteRarity(rarity, fieldMappingService.mapRarity(localization, rarityCode))
        );
    }

    private LocalDate parseDate(String date) {
        return StringUtils.isNotBlank(date) ? LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
    }

    @Nonnull
    private static String getSetName(Element element) {
        var name = element.select("p").text();

        for (var sneakPeek : SNEAK_PEEKS) {
            name = StringUtils.removeIgnoreCase(name, sneakPeek);
        }
        for (var specialEdition : SPECIAL_EDITIONS) {
            name = StringUtils.removeIgnoreCase(name, specialEdition);
        }
        return WordUtils.capitalizeFully(PCAUtils.clean(name));
    }

    private Element getSetPage(String pid, Localization localization) throws IOException {
        return get(getUriBuilder("card_search.action", localization)
                .queryParam("ope", "1")
                .queryParam("sess", "1")
                .queryParam("pid", pid)
                .queryParam("rp", "99999")
                .build());
    }

    private Element getCardPage(String cid, Localization localization) throws IOException {
        return get(getCardUrl(cid, localization));
    }

    @Nonnull
    public URI getCardUrl(String cid, Localization localization) {
        return getUriBuilder("card_search.action", localization)
                .queryParam("ope", "2")
                .queryParam("cid", cid)
                .build();
    }

    @Nonnull
    private Element get(String url) throws IOException {
        return get(url, Localization.USA);
    }

    @Nonnull
    private Element get(String url, Localization localization) throws IOException {
        return get(getUriBuilder(url, localization));
    }

    @Nonnull
    private Element get(UriBuilder builder) throws IOException {
        return get(builder.build());
    }

    @Nonnull
    private Element get(URI uri) throws IOException {
        return htmlParser.get(uri.toString()).body();
    }

    @Nonnull
    private UriBuilder getUriBuilder(String url, Localization localization) {
        var builder = uriBuilderFactory.uriString(htmlParser.processUrl(this.dbYuGiOhComUrl, url));

        builder.queryParam("request_locale", getLocalCode(localization));
        return builder;
    }

    private String getLocalCode(Localization localization) {
        if (localization == Localization.USA) {
            return "en";
        } else if (localization == Localization.JAPAN) {
            return "ja";
        }else if (localization == Localization.KOREA) {
            return "ko";
        }
        return localization.getCode();
    }

    private record Page2SetLine(
        LocalDate releaseDate,
        String number,
        String setPid,
        OfficialSiteRarity rarity
    ) {
        public Foil foil() {
            return rarity.foil();
        }
    }
}
