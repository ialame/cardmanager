package com.pcagrade.retriever.card.yugioh.source.yugipedia;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.cache.CacheService;
import com.pcagrade.retriever.card.yugioh.YuGiOhFieldMappingService;
import com.pcagrade.retriever.card.yugioh.source.yugipedia.set.YugipediaSetDTO;
import com.pcagrade.retriever.date.DateHelper;
import com.pcagrade.retriever.parser.wiki.WikiParser;
import com.pcagrade.retriever.parser.wiki.WikiParserHelper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sweble.wikitext.parser.nodes.WtNode;
import org.sweble.wikitext.parser.nodes.WtTemplate;
import org.sweble.wikitext.parser.nodes.WtTemplateArgument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class YugipediaParser {

    private static final String SET_LIST = "Set list";
    private static final String SET_LIST_TABS = "Set list tabs";
    private static final String INFOBOX_SET = "Infobox set";
    private static final String CARD_TABLE = "CardTable2";
    private static final String NAVBOX = "navbox";
    private static final String SET_NAVBOX_TEMPLATES = "Category:Set_navbox_templates";
    private static final List<String> EXCLUDED_NAVBOXES = List.of(
            "Template:Saikyō Jump promotional cards",
            "Template:Shonen Jump promotional cards",
            "Template:V Jump promotional cards",
            "Template:V Jump mail orders",
            "Template:Weekly Shōnen Jump promotional cards",
            "Template:Unreleased sets"
    );
    private static final Flux<String> SPECIAl_SETS = Flux.just(
            "Saikyō Jump promotional cards",
            "Shonen Jump promotional cards",
            "V Jump promotional cards",
            "Weekly Shōnen Jump promotional cards"
    );
    private static final Map<Localization, String> ANNIVERSARY_SUFFIXES = Map.of(
            Localization.USA, " (lc01 25th Anniversary Edition)",
            Localization.FRANCE, " (lc01 Édition 25e Anniversaire)",
            Localization.GERMANY, " (lc01 25th Anniversary Edition)",
            Localization.ITALY, " (lc01 Edizione 25°anniversario)",
            Localization.SPAIN, " (lc01 Edición 25° Aniversario)",
            Localization.PORTUGAL, " (lc01 Edição do 25° Aniversário)"
    );
    private static final Map<Localization, List<String>> CODES = Map.of(
            Localization.USA, List.of("en", "na", "ae", "au", "eu", "oc"),
            Localization.SPAIN, List.of("sp", "es"),
            Localization.FRANCE, List.of("fr", "fc"),
            Localization.JAPAN, List.of("jp", "ja"),
            Localization.KOREA, List.of("kr", "ko")
    );
    public static final String NAME_SUFFIX = "_name";

    private final WikiParser wikiParser;

    @Autowired
    private YuGiOhFieldMappingService fieldMappingService;

    public YugipediaParser(@Autowired ObjectMapper objectMapper, @Autowired CacheService cacheService) {
        this.wikiParser = new WikiParser(objectMapper, cacheService, "https://yugipedia.com/api.php");
        this.wikiParser.setUseSlots(false);
    }


    public List<YugipediaCard> getCards(String url, Localization localization) {
        return wikiParser.parsePage(url)
                .flatMapMany(p -> getCardLines(localization, p))
                .collectList()
                .block();
    }

    private Flux<YugipediaCard> getCardLines(Localization localization, WtNode node) {
        return Mono.justOrEmpty(getCardListTemplate(node))
                .flatMapMany(template -> {
                    var text = WikiParserHelper.getAsString(WikiParserHelper.getArgument(template, ""));
                    var globalRarities = List.of(WikiParserHelper.getAsString(WikiParserHelper.getArgument(template, "rarities")).split(","));

                    return Flux.fromStream(text.lines())
                            .filter(StringUtils::isNotBlank)
                            .flatMap(l -> createCards(localization, l, globalRarities));
                });
    }

    private Flux<YugipediaCard> createCards(Localization localization, String line, List<String> globalRarities) {
        var split = StringUtils.substringBefore(line, "//").split(";");
        var number = fieldMappingService.mapNumber(localization, getFromSplit(split, 0));
        var name = getFromSplit(split, 1);

        if (StringUtils.isBlank(name)) {
            return Flux.empty();
        }
        return getCardNameInPage2(name, localization)
                .flatMapMany(n -> {
                    var s = getFromSplit(split, 2);

                    return (StringUtils.isNotBlank(s) ? Flux.fromArray(s.split(",")) : Flux.fromIterable(globalRarities))
                            .map(PCAUtils::clean)
                            .filter(StringUtils::isNotBlank)
                            .map(fieldMappingService.forRarity(localization))
                            .distinct()
                            .map(r -> new YugipediaCard(localization, number, n, r))
                            .collectList()
                            .map(l -> l.isEmpty() ? List.of(new YugipediaCard(localization, number, n, "C")) : l)
                            .flatMapIterable(l -> l);
                });
    }

    private String getFromSplit(String[] split, int index) {
        return split.length > index ? PCAUtils.clean(split[index]) : "";
    }

    private Mono<String> getCardNameInPage2(String usName, Localization localization) {
        return wikiParser.parsePage(usName)
                .flatMap(p -> Mono.justOrEmpty(WikiParserHelper.findTemplate(p, CARD_TABLE)))
                .map(t -> {
                    var newName = findArgument(t, localization, NAME_SUFFIX);

                    return StringUtils.isNotBlank(newName) ? newName : usName;
                });
    }

    private Optional<WtTemplate> getCardListTemplate(WtNode page) {
        return WikiParserHelper.findTemplate(page, SET_LIST, t -> StringUtils.isNotBlank(WikiParserHelper.getAsString(WikiParserHelper.getArgument(t, ""))));
    }

    public List<ParsedYugipediaSet> getAllSets() {
        return wikiParser.listCategory(SET_NAVBOX_TEMPLATES)
                .filter(s -> EXCLUDED_NAVBOXES.stream().noneMatch(e -> StringUtils.equalsIgnoreCase(e, s)))
                .flatMap(this::getSetPages)
                .concatWith(SPECIAl_SETS)
                .distinct()
                .flatMap(this::getSet)
                .collectList()
                .block();
    }

    private Mono<ParsedYugipediaSet> getSet(String url) {
        return wikiParser.parsePage(url)
                .flatMap(p -> {
                    var infoboxOpt = WikiParserHelper.findTemplate(p, INFOBOX_SET);

                    if (infoboxOpt.isEmpty()) {
                        return Mono.empty();
                    }

                    var infobox = infoboxOpt.get();
                    var setName = findArgument(infobox, Localization.USA, NAME_SUFFIX);

                    if (StringUtils.isBlank(setName)) {
                        setName = url;
                    }

                    var yugipediaSets = getYugipediaSets(url, p);
                    var translations = new EnumMap<Localization, ParsedYugipediaSetTranslation>(Localization.class);
                    var type = WikiParserHelper.parseLink(WikiParserHelper.getArgument(infobox, "type")).getRight();
                    var mainSet = WikiParserHelper.getArgumentAsString(infobox, "main_set");
                    var mainPrefix = trimPrefix(findArgument(infobox, List.of("prefix", "tcg_prefix", "ocg_prefix"), ""));

                    if (StringUtils.equalsIgnoreCase(mainSet, "yes")) {
                        mainSet = StringUtils.substringBefore(setName, ":");
                    }
                    for (var l : Localization.Group.YUGIOH) {
                        var codes = getCodes(l);
                        var name = findArgument(infobox, codes, NAME_SUFFIX);

                        if (StringUtils.isBlank(name)) {
                            if (yugipediaSets.stream().noneMatch(s -> s.localization() == l)) {
                                continue;
                            }
                            name = setName;
                        } else if (is25thAnniversary(url) && !is25thAnniversary(name)) {
                            name += ANNIVERSARY_SUFFIXES.get(l);
                        }

                        var pid = findArgument(infobox, codes, "_database_id");
                        var prefix = trimPrefix(findArgument(infobox, codes, "_prefix"));
                        var releaseDate = getReleaseDate(infobox, codes);

                        translations.put(l, new ParsedYugipediaSetTranslation(l, name, StringUtils.isNotBlank(prefix) ? prefix : mainPrefix, pid, releaseDate));
                    }
                    if (translations.isEmpty()) {
                        return Mono.empty();
                    }
                    return Mono.just(new ParsedYugipediaSet(setName, type, mainPrefix, mainSet, translations, List.copyOf(yugipediaSets), "https://yugipedia.com/wiki/" + url));
                });
    }

    private static boolean is25thAnniversary(String name) {
        return StringUtils.containsAnyIgnoreCase(name, "25th", "25e", "25°");
    }

    private static String findArgument(WtTemplate infobox, Localization localization, String suffix) {
        return findArgument(infobox, getCodes(localization), suffix);
    }

    private static String findArgument(WtTemplate infobox, Collection<String> codes, String suffix) {
        return findArguments(infobox, codes, suffix).stream()
                .findFirst()
                .orElse("");
    }

    private static List<String> findArguments(WtTemplate infobox, Collection<String> codes, String suffix) {
        var args = WikiParserHelper.getArguments(infobox);

        return codes.stream()
                .<String>mapMulti((code, downstream) -> {
                    var pattern = Pattern.compile("[A-z/]*" + code + "[A-z/]*" + suffix + "$");

                    args.stream()
                            .filter(a -> pattern.matcher(WikiParserHelper.getAsString(a)).matches())
                            .map(a -> WikiParserHelper.getAsString(a.getValue()))
                            .filter(StringUtils::isNotBlank)
                            .forEach(downstream);
                })
                .toList();
    }

    private static String trimPrefix(String prefix) {
        return StringUtils.substringBefore(prefix, "-");
    }

    @Nullable
    private static LocalDate getReleaseDate(WtTemplate infobox, Collection<String> codes) {
        for (var text : findArguments(infobox, codes, "_release_date")) {
            var date = DateHelper.parseDate(text);

            if (date != null) {
                return date;
            }
        }
        return null;
    }

    @Nonnull
    private List<YugipediaSetDTO> getYugipediaSets(String url, WtNode p) {
        var sets = new LinkedList<YugipediaSetDTO>();

        WikiParserHelper.findTemplate(p, SET_LIST_TABS).ifPresent(t -> {
            var list = WikiParserHelper.getAsString(WikiParserHelper.getArgument(t, "")).split(",");

            for (var s : list) {
                sets.add(new YugipediaSetDTO(cretePageLink(url, s), getLocalization(s)));
            }
        });
        return new ArrayList<>(sets);
    }

    private static List<String> getCodes(Localization localization) {
        var list = CODES.get(localization);

        return CollectionUtils.isNotEmpty(list) ? list : List.of(localization.getCode());
    }
    private Localization getLocalization(String code) {
        var lowerCaseCode = StringUtils.lowerCase(code);

        return CODES.entrySet().stream()
                .filter(e -> e.getValue().contains(lowerCaseCode))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseGet(() -> Localization.getByCode(lowerCaseCode));
    }

    private String cretePageLink(String pageName, String code) {
        return "Set_Card_Lists:" +  StringUtils.replaceIgnoreCase(pageName, " ", "_") + "_(" + getMediumName(code) + ")";
    }

    private String getMediumName(String code) {
        return "TCG-" + StringUtils.upperCase(code);
    }

    // TODO visible for testing
    Flux<String> getSetPages(String page) {
        return wikiParser.parsePage(page)
                .flatMapMany(this::parseSetNavBox);
    }

    private Flux<String> parseSetNavBox(WtNode page) {
        return Flux.fromStream(WikiParserHelper.findAllNodes(page, n -> n instanceof WtTemplate template && WikiParserHelper.templateIs(template, NAVBOX)).stream()
                .map(WtTemplate.class::cast)
                .<WtTemplateArgument>mapMulti((t, downstream) -> WikiParserHelper.getArguments(t).forEach(downstream)))
                .filter(YugipediaParser::isValidSetNameList)
                .map(t -> WikiParserHelper.getAsString(t.getValue()))
                .filter(StringUtils::isNotBlank)
                .flatMap(s -> Flux.fromStream(s.lines()))
                .map(s -> WikiParserHelper.parseLink(s).getLeft())
                .filter(StringUtils::isNotBlank)
                .distinct();
    }

    private static boolean isValidSetNameList(WtTemplateArgument t) {
        var name = WikiParserHelper.getAsString(t);

        return StringUtils.startsWith(name, "list") && !StringUtils.equalsIgnoreCase(name, "listclass");
    }
}
