package com.pcagrade.retriever.card.pokemon.source.bulbapedia.parser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.cache.CacheService;
import com.pcagrade.retriever.card.pokemon.PokemonCardHelper;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.BulbapediaException;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.BulbapediaPokemonCard;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.BulbapediaPokemonCardPage2;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction.BulbapediaExtractionFeature;
import com.pcagrade.retriever.card.pokemon.tag.TeraType;
import com.pcagrade.retriever.image.ExtractedImageDTO;
import com.pcagrade.retriever.parser.RetrieverHTTPHelper;
import com.pcagrade.retriever.parser.wiki.WikiParserHelper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.sweble.wikitext.parser.nodes.WtInternalLink;
import org.sweble.wikitext.parser.nodes.WtNode;
import org.sweble.wikitext.parser.nodes.WtTemplate;
import org.sweble.wikitext.parser.nodes.WtText;
import org.sweble.wikitext.parser.nodes.WtValue;
import org.sweble.wikitext.parser.utils.NonExpandingParser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import xtc.parser.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BulbapediaApiParser implements IBulbapediaParser {

    private static final Logger LOGGER = LogManager.getLogger(BulbapediaApiParser.class);


    private static final String WIKI = "/wiki/";

    private static final String BR = "<br>";
    private static final String FILE_PREFIX = "File:";
    private static final String TCG = "TCG";
    private static final String TCG_ID = "TCG ID";
    private static final String OBP = "OBP";
    private static final String HALF_DECK_LIST_ENTRY = "Halfdecklist/entry";
    private static final String HALF_DECK_LIST_NWENTRY = "Halfdecklist/nmentry";
    private static final String TERASTAL = "Cardtext/Terastal";
    private static final List<String> HALF_DECK_LIST_ENTRIES = List.of(HALF_DECK_LIST_ENTRY, HALF_DECK_LIST_NWENTRY);
    private static final List<String> HEADER_TEMPLATES = List.of("Setlist/header", "Setlist/nmheader", "Halfdecklist/header", "Halfdecklist/nmheader");
    private static final List<String> ENTRY_TEMPLATES = List.of("Setlist/entry", "Setlist/nmentry", HALF_DECK_LIST_ENTRY, HALF_DECK_LIST_NWENTRY);
    private static final List<String> FOOTER_TEMPLATES = List.of("Setlist/footer", "Setlist/nmfooter", "Halfdecklist/footer", "Halfdecklist/nmfooter");

    private static final List<String> OFFSETED_ENTRIES = List.of("Setlist/entry", HALF_DECK_LIST_ENTRY);

    public static final List<String> INFOBOX_HEADER = List.of("PokémoncardInfobox", "TCGTrainerCardInfobox", "TCGEnergyCardInfobox");

    public static final List<String> INFOBOX_EXPANSION = List.of("PokémoncardInfobox/Expansion", "TCGTrainerCardInfobox/Expansion", "TCGEnergyCardInfobox/Expansion");

    public static final List<String> INFOBOX_FOOTER = List.of("PokémoncardInfobox/Footer", "TCGTrainerCardInfobox/Footer", "CGEnergyCardInfobox/Footer");

    public static final List<String> INFOBOX_RELEASE_INFO = List.of("TCGTrainerCardInfobox/ReleaseInfo", "TCGEnergyCardInfobox/ReleaseInfo");
    private static final List<String> INFOBOX_TEMPLATES = List.copyOf(ListUtils.union(INFOBOX_HEADER, INFOBOX_EXPANSION));

    private static final List<String> TEMPLATES_WITH_IMAGE;

    static {
        var list = new ArrayList<String>(HEADER_TEMPLATES.size() + INFOBOX_HEADER.size() + 1);

        list.addAll(HEADER_TEMPLATES);
        list.addAll(INFOBOX_HEADER);
        list.add("TCGGallery");
        TEMPLATES_WITH_IMAGE = List.copyOf(list);
    }

    private static final List<String> IMAGE_ARGUMENTS = List.of("image", "reprint");

    private static final List<String> DELTA_SPECIES_LEVELS = List.of("δ", "exδ", "starδ");

    private static final List<Pattern> NUMBER_CLEANUP_PATTERNS = List.of(
            Pattern.compile("\\((top|bottom) (left|right)\\)$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("^\\[\\[.*]]", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)
    );

    private static final Map<Localization, List<String>> LOCALIZATION_EXPANSION_ARGUMENTS = Map.of(
            Localization.USA, List.of("expansion", "deckkit", "halfdeck"),
            Localization.JAPAN, List.of("jpexpansion", "jpdeckkit", "jpdeck", "jphalfdeck", "jpsidedeck", "jpthemedeck", "jpquarterdeck"));

    private static final Map<String, BiFunction<BulbapediaApiParser, WtTemplate, String>> GENERIC_TEMPLATE_READERS = Map.of(
            TCG, (p, t) -> p.parseTCGTemplate(t).getLeft(),
            "g", BulbapediaApiParser::parseFirstPrefixedByPokemonPTemplate,
            "wp", BulbapediaApiParser::parse2Or1Template,
            "single", BulbapediaApiParser::parse2Or1Template,
            "e", BulbapediaApiParser::parseEnergyTemplate,
            "tt", (p, t) -> WikiParserHelper.parseFirstArgument(t),
            "SP", BulbapediaApiParser::parseFirstPrefixedByPokemonPTemplate,
            "mov", BulbapediaApiParser::parse2Or1Template
    );

    private static final List<String> BRACKET_OPENERS = List.of("<small>'''", "'''<small>");
    private static final List<String> BRACKET_CLOSERS = List.of("'''", "</small>");
    private static final List<Pair<String, String>> BRACKET_SEPARATORS = BRACKET_OPENERS.stream()
            .flatMap(opener -> BRACKET_CLOSERS.stream().map(closer -> Pair.of(opener, closer)))
            .toList();

    private static final Pattern BRACKET_PATTERN = Pattern.compile("^\\[[A-Z\\s\\d']*]$", Pattern.CASE_INSENSITIVE);

    private static final List<String> BRACKET_IDENTIFIERS = List.copyOf(ListUtils.union(ListUtils.union(BRACKET_OPENERS, BRACKET_CLOSERS), List.of("[[", "]]")));

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CacheService cacheService;

    @Value("${bulbapedia.url}")
    private String bulbapediaWikiUrl;

    private final String urlBulbapediaApi;
    private final WebClient webClient;
    private final NonExpandingParser parser;

    public BulbapediaApiParser(String maxInMemorySize, String urlBulbapediaApi) {
        this.webClient = WebClient.builder()
                .baseUrl(urlBulbapediaApi)
                .exchangeStrategies(RetrieverHTTPHelper.createExchangeStrategies(maxInMemorySize))
                .clientConnector(RetrieverHTTPHelper.createReactorClientHttpConnector("bulbapedia-api-connection-provider", 2))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        this.parser = new NonExpandingParser();
        this.urlBulbapediaApi = urlBulbapediaApi;
    }

    @Nonnull
    @Override
    public List<BulbapediaPokemonCard> parseExtensionPage(String url, String tableName, String h3Name, String firstNumber, boolean promo) {
        return PCAUtils.fluxToList(parsePage(url)
                .flatMapIterable(page -> getCardEntries(tableName, h3Name, firstNumber, page))
                .flatMap(entry -> parseEntry(entry, promo))
                .flatMapIterable(this::splitBrackets));
    }

    @Nonnull
    private List<WtTemplate> getCardEntries(String tableName, String h3Name, String firstNumber, WtNode page) {
        return getCardEntries(tableName, h3Name, firstNumber, StringUtils.isBlank(h3Name), page);
    }

    @Nonnull
    private List<WtTemplate> getCardEntries(String tableName, String h3Name, String firstNumber, boolean inH3, WtNode page) {
        if (WikiParserHelper.isNodeEmpty(page)) {
            return Collections.emptyList();
        }

        var index = -1;

        for (int i = 0; i < page.size(); i++) {
            var node = page.get(i);
            if (node instanceof WtTemplate template) {
                if (inH3 && WikiParserHelper.templateIs(template, HEADER_TEMPLATES) && tableName.equalsIgnoreCase(WikiParserHelper.getAsString(WikiParserHelper.getArgument(template, "title"))) && checkFirstNumber(page, i + 1, firstNumber)) {
                    index = i + 1;
                    break;
                }
            } else {
                var entries = getCardEntries(tableName, h3Name, firstNumber, inH3 || StringUtils.equalsIgnoreCase(h3Name, WikiParserHelper.getAsString(node)), node);

                if (!entries.isEmpty()) {
                    return entries;
                }
            }
        }

        if (index == -1) {
            return Collections.emptyList();
        }

        var value = new ArrayList<WtTemplate>(page.size());

        for (int i = index; i < page.size(); i++) {
            var node = page.get(i);

            if (node instanceof WtTemplate template) {
                if (WikiParserHelper.templateIs(template, ENTRY_TEMPLATES)) {
                    value.add(template);
                } else if (WikiParserHelper.templateIs(template, FOOTER_TEMPLATES)) {
                    break;
                }
            }
        }
        return value;
    }

    private boolean checkFirstNumber(WtNode page, int index, String firstNumber) {
        if (StringUtils.isBlank(firstNumber) || PokemonCardHelper.isNoNumber(firstNumber)) {
            return true;
        }

        for (int i = index; i < page.size(); i++) {
            var node = page.get(i);
            if (node instanceof  WtTemplate template) {
                if (WikiParserHelper.templateIs(template, ENTRY_TEMPLATES)) {
                    return StringUtils.equalsIgnoreCase(firstNumber, fixNumber(WikiParserHelper.parseFirstArgument(template)));
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    private Flux<BulbapediaPokemonCard> parseEntry(WtTemplate template, boolean promo) {
        if (template == null) {
            return Flux.empty();
        }

        var offset = getOffset(template);
        var card = new BulbapediaPokemonCard();

        card.setNumber(fixNumber(WikiParserHelper.parseFirstArgument(template)));
        if (PokemonCardHelper.isAlternate(card.getNumber())) {
            card.setAlternate(true);
        }
        card.setType(WikiParserHelper.getAsString(WikiParserHelper.getArgument(template, 2 + offset)));
        card.setType2(WikiParserHelper.getAsString(WikiParserHelper.getArgument(template, 3 + offset)));
        if (!WikiParserHelper.templateIs(template, HALF_DECK_LIST_ENTRIES)) {
            card.setRarity(fixRarity(WikiParserHelper.getAsString(WikiParserHelper.getArgument(template, 4 + offset))));
        } else {
            card.setRarity("");
        }
        if (StringUtils.isBlank(card.getRarity()) && promo) {
            card.setRarity("Promo");
        }

        Flux<BulbapediaPokemonCard> flux = parseName(card, WikiParserHelper.getArgument(template, 1 + offset));

        if (promo) {
            return parsePromo(flux, WikiParserHelper.getArgument(template, 5 + offset));
        }
        return flux;
    }

    private int getOffset(WtTemplate template) {
        if (!WikiParserHelper.templateIs(template, OFFSETED_ENTRIES)) {
            return 0;
        }

        var value = WikiParserHelper.getAsString(WikiParserHelper.getArgument(template, 1));

        return value.length() == 1 ? 1 : 0;
    }


    @Nonnull
    private Flux<BulbapediaPokemonCard> parsePromo(Flux<BulbapediaPokemonCard> flux, WtValue value) {
        return flux.collectList().flatMapMany(cards -> {
            List<String> promoParts = new ArrayList<>();

            List<String> promos = value.stream()
                    .<String>mapMulti((n, downstream) -> {
                        String v;

                        if (n instanceof WtTemplate promoTemplate) {
                            if (WikiParserHelper.templateIs(promoTemplate, "TCGMerch")) {
                                var merch = WikiParserHelper.getAsString(WikiParserHelper.getArgument(promoTemplate, 3));

                                v = StringUtils.isNotBlank(merch) ? merch : WikiParserHelper.getAsString(WikiParserHelper.getArgument(promoTemplate, 2));
                            } else {
                                v = parseGenericTemplate(promoTemplate);
                            }
                        } else {
                            v = WikiParserHelper.getAsString(n);
                        }
                        pushPart(promoParts, fixBR(v), downstream);
                    })
                    .collect(Collectors.toList());

            if (!promoParts.isEmpty()) {
                promos.add(collectParts(promoParts));
            }
            promos.removeIf(StringUtils::isBlank);

            if (!cards.isEmpty() && !promos.isEmpty()) {
                if (cards.stream()
                        .mapToInt(BulbapediaPokemonCard::getLineCount)
                        .sum() == promos.size()) {
                    IntStream.range(0, promos.size()).forEach(i -> {
                        var line = 0;

                        for (var card : cards) {
                            line += card.getLineCount();

                            if (i < line) {
                                card.addPromo(promos.get(i));
                                break;
                            }
                        }
                    });
                } else {
                    cards.forEach(c -> {
                        var cardPromos = c.getPromos();

                        cardPromos.addAll(promos);
                    });
                }
            }
            return Flux.fromIterable(cards).filter(c -> !c.getPromos().isEmpty());
        });
    }

    private Flux<BulbapediaPokemonCard> parseName(BulbapediaPokemonCard source, WtValue value) {
        if (value.isEmpty()) {
            return Flux.empty();
        }

        var card = new AtomicReference<>(new BulbapediaPokemonCard());
        List<String> cardNameParts = new ArrayList<>();

        BeanUtils.copyProperties(source, card.get());

        var list = value.stream()
                .<BulbapediaPokemonCard>mapMulti((n, downstream) -> {
                    String v = "";
                    var c = card.get();

                    if (n instanceof WtTemplate template) {
                        if (WikiParserHelper.templateIs(template, TCG_ID)) {
                            v = WikiParserHelper.getAsString(WikiParserHelper.getArgument(template, 1));
                            c.setLink(buildWikiUrl(encodeUrl(v + "_(" + WikiParserHelper.parseFirstArgument(template) + "_" + WikiParserHelper.getAsString(WikiParserHelper.getArgument(template, 2)) + ")")));
                        } else if (WikiParserHelper.templateIs(template, TCG)) {
                            var pair = parseTCGTemplate(template);

                            v = pair.getLeft();
                            c.setLink(buildWikiUrl(pair.getRight()));
                        } else if (WikiParserHelper.templateIs(template, OBP)) {
                            var pair = parseOBPTemplate(template);

                            v = pair.getLeft();
                            c.setLink(buildWikiUrl(encodeUrl(v + " (" + pair.getRight() + ")")));
                        } else if (WikiParserHelper.templateIs(template, "e")) {
                            v = parseEnergyTemplate(template);
                        } else {
                            var name = parseGenericTemplate(template);

                           c.addFeature(new BulbapediaExtractionFeature(name, name));
                           v = "{" + (c.getFeatures().size() - 1) + "}";
                        }
                    } else if (n instanceof WtInternalLink link && link.hasTitle()) {
                        v = WikiParserHelper.getAsString(link);
                        c.setLink(buildWikiUrl(encodeUrl(WikiParserHelper.getAsString(link.getTitle()))));
                    } else if (n instanceof WtText text) {
                        parseLiteralName(source, card, cardNameParts, downstream, fixBR(WikiParserHelper.getAsString(text)));
                    } else {
                        v = WikiParserHelper.getAsString(n);
                    }

                    pushNamePartCard(source, card, cardNameParts, downstream, fixBR(v));
                }).collect(Collectors.toList());

        if (!cardNameParts.isEmpty()) {
            card.get().setName(collectParts(cardNameParts));
            list.add(card.get());
        }

        return Flux.fromIterable(list)
                .filter(c -> StringUtils.isNotBlank(c.getName()) && (!PokemonCardHelper.isNoNumber(c.getNumber()) || !c.getName().contains("Energy") || !c.getType().equals("Energy")) && c.getBrackets().stream().noneMatch(b -> StringUtils.equalsIgnoreCase(b, "jumbo")));
    }

    private void parseLiteralName(BulbapediaPokemonCard source, AtomicReference<BulbapediaPokemonCard> card, List<String> cardNameParts, Consumer<BulbapediaPokemonCard> downstream, String text) {
        String value = "";

        if (BR.equalsIgnoreCase(text)) {
            value = text;
        } else if (text.contains(BR)) {
            var split = text.split(BR);

            for (int i = 0; i < split.length; i++) {
                parseLiteralName(source, card, cardNameParts, downstream, i < split.length - 1 ? split[i] + BR: split[i]);
            }
            value = extractBRs(text);
        } else if (StringUtils.containsIgnoreCase(text, "[jumbo]")) {
            cardNameParts.clear();
        } else if (BRACKET_IDENTIFIERS.stream().anyMatch(s -> StringUtils.containsIgnoreCase(text, s))) {
            var split = PCAUtils.substringBetween(text, "[[", "]]").split("\\|");

            if (split.length == 1) {
                value = split[0];
            } else if (split.length == 2) {
                value = split[1];
                card.get().setLink(buildWikiUrl(encodeUrl(split[0])));
            }

            var v = value;

            BRACKET_SEPARATORS.stream()
                    .<String>mapMulti((p, d) -> PCAUtils.substringsBetween(text, p.getLeft(), p.getRight()).forEach(d))
                    .map(this::clean)
                    .distinct()
                    .filter(b -> !StringUtils.containsIgnoreCase(v, b))
                    .forEach(b -> {
                        var trimmed = PCAUtils.substringBetween(b, "[", "]");

                        if (StringUtils.isBlank(trimmed)) {
                            trimmed = b;
                        }
                        var splitBracket = trimmed.split(";");

                        for (var s : splitBracket) {
                            var t = PCAUtils.clean(s);

                            if (StringUtils.isNotBlank(t)) {
                                card.get().addBracket(t);
                            }
                        }
                    });
        } else if (BRACKET_PATTERN.matcher(text).matches()) {
            var trimmed = PCAUtils.substringBetween(text, "[", "]");

            if (StringUtils.isNotBlank(trimmed)) {
                card.get().addBracket(trimmed);
            }
        } else {
            value = text;
        }
        pushNamePartCard(source, card, cardNameParts, downstream, value);
    }

    @Nonnull
    private static String extractBRs(String text) {
        var valueBuilder = new StringBuilder();

        while (text.endsWith(BR)) {
            text = StringUtils.removeEnd(text, BR);
            valueBuilder.append(BR);
        }
        return valueBuilder.toString();
    }

    @Nonnull
    private static String fixBR(String text) {
        if (StringUtils.isBlank(text)) {
            return "";
        }
        return RegExUtils.replaceAll(text, "<br[\s/]*>", BR);
    }

    private void pushNamePartCard(BulbapediaPokemonCard source, AtomicReference<BulbapediaPokemonCard> card, List<String> cardNameParts, Consumer<BulbapediaPokemonCard> downstream, String v) {
        int lineCount = countLines(v);

        pushPart(cardNameParts, lineCount > 1 ? BR : v, s -> {
            var c2 = card.get();

            c2.setName(s);
            c2.setLineCount(lineCount);
            downstream.accept(c2);
            card.set(new BulbapediaPokemonCard());
            BeanUtils.copyProperties(source, card.get());
        });
    }

    private static int countLines(String t) {
        var lineCount = 0;

        while (t.endsWith(BR)) {
            t = StringUtils.removeEnd(t, BR);
            lineCount++;
        }
        return lineCount > 0 ? lineCount : 1;
    }

    private void pushPart(List<String> parts, String v, Consumer<String> downstream) {
        if (StringUtils.isBlank(v)) {
            return;
        } else if (BR.equalsIgnoreCase(v)) {
            downstream.accept(collectParts(parts));
            parts.clear();
            return;
        }

        var split = v.split(BR);

        if (split.length > 0) {
            pushPart(parts, split[0]);
            for (int i = 1; i < split.length; i++) {
                downstream.accept(collectParts(parts));
                parts.clear();
                pushPart(parts, split[i]);
            }
            if (v.endsWith(BR)) {
                downstream.accept(collectParts(parts));
                parts.clear();
            }
        } else {
            pushPart(parts, v);
        }
    }

    private void pushPart(List<String> parts, String v) {
        var trimed = StringUtils.trimToEmpty(v);

        if (StringUtils.contains(trimed, "|")) {
            var split = trimed.split("\\|");

            trimed = split[split.length - 1];
        }
        if (StringUtils.isBlank(trimed)) {
            return;
        }
        parts.add(trimed);
    }

    private String collectParts(List<String> parts) {
        return clean(StringUtils.join(parts, " "));
    }

    private String clean(String s) {
        if (StringUtils.isBlank(s)) {
            return "";
        }

        return PCAUtils.clean(s.replace(BR, "")
                .replace("[[", "")
                .replace("]]", "")
                .replace("'''", "")
                .replace("''", "")
                .replace("<small>", "")
                .replace("</small>", ""));
    }

    private String fixNumber(String number) {
        for (var pattern : NUMBER_CLEANUP_PATTERNS) {
            var matcher = pattern.matcher(number);

            if (matcher.find()) {
                number = matcher.replaceAll("");
            }
        }
        number = StringUtils.trim(number);

        if (PokemonCardHelper.isNoNumber(number)) {
            return PokemonCardHelper.NO_NUMBER;
        }
        return number;
    }

    private String fixRarity(String rarity) {
        var pattern = Pattern.compile("File:Rarity ([A-z\\s]*).png");
        var matcher = pattern.matcher(rarity);

        if (matcher.find()) {
            return PCAUtils.substringBetween(rarity, "File:Rarity ", ".png");
        }
        return "None".equals(rarity) ? "—" : rarity;
    }

    private List<BulbapediaPokemonCard> splitBrackets(BulbapediaPokemonCard c) {
        var list = new ArrayList<BulbapediaPokemonCard>();

        var pattern = Pattern.compile("\"\\d+\"\\sand\\s\"\\d+\"");
        var brackets = c.getBrackets();

        for (int i = 0; i < brackets.size(); i++) {
            var bracket = brackets.get(i);
            var matcher = pattern.matcher(bracket);

            if (matcher.find()) {
                var groups = matcher.group().split("\\sand\\s");
                var suffix = matcher.replaceAll("");

                for (var group : groups) {
                    var card = new BulbapediaPokemonCard();

                    BeanUtils.copyProperties(c, card);
                    c.getFeatures().forEach(card::addFeature);
                    c.getPromos().forEach(card::addPromo);
                    brackets.forEach(card::addBracket);
                    card.getBrackets().set(i, group + suffix);
                    list.add(card);
                }
                return list;
            }
        }
        list.add(c);
        return list;
    }

    @Override
    public boolean isInPage2(String url, String number, Localization localization, String setName, boolean unnumbered) {
        return Boolean.TRUE.equals(getInfoboxExtension(url, number, localization, setName, unnumbered).hasElements().block());
    }

    @Nonnull
    @Override
    public String findOriginalName(String url) {
        return clean(getInfoboxArgumentAsString(url, "jname"));
    }

    @Nonnull
    @Override
    public String findForme(String url) {
        return clean(getInfoboxArgumentAsString(url, "forme"));
    }

    @Override
    public int findLevel(String url) {
        try {
            var value = clean(getInfoboxArgumentAsString(url, "level"));

            if (StringUtils.isBlank(value)) {
                return 0;
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Nonnull
    @Override
    public String findArtistName(String url) {
        var caption = getInfoboxArgument(url, "caption");

        for (var n : caption) {
            var content = WikiParserHelper.getAsString(n);

            if (!StringUtils.containsIgnoreCase(content, "Illus.")) {
                continue;
            }

            var split = PCAUtils.substringBetween(WikiParserHelper.getAsString(n), "Illus. [[", "]]").split("\\|");

            if (split.length >= 2) {
                return StringUtils.trimToEmpty(split[1]);
            } else if (split.length == 1) {
                return StringUtils.trimToEmpty(split[0]);
            }
        }
        return "";
    }

    @Nullable
    @Override
    public TeraType findTeraType(String url) {
        return parsePage(url)
                .flatMapIterable(this::getTerastal)
                .flatMap(template -> {
                    var type = WikiParserHelper.getAsString(WikiParserHelper.getArgument(template, "type"));

                    for (var t : TeraType.values()) {
                        if (StringUtils.equalsIgnoreCase(t.getName(), type)) {
                            return Mono.just(t);
                        }
                    }
                    return Mono.empty();
                }).next().block();
    }

    @Nonnull
    private List<WtTemplate> getTerastal(WtNode page) {
        if (WikiParserHelper.isNodeEmpty(page)) {
            return Collections.emptyList();
        }

        var templates = new ArrayList<WtTemplate>();

        for (WtNode node : page) {
            if (node instanceof WtTemplate template) {
                if (WikiParserHelper.templateIs(template, TERASTAL)) {
                    templates.add(template);
                }
            } else {
                var entries = getTerastal(node);

                if (!entries.isEmpty()) {
                    templates.addAll(entries);
                }
            }
        }

        return templates;
    }

    @Override
    public boolean isDeltaSpecies(String url) {
        return DELTA_SPECIES_LEVELS.contains(getInfoboxArgumentAsString(url, "level"));
    }

    private String getInfoboxArgumentAsString(String url, String arg) {
        return StringUtils.trimToEmpty(WikiParserHelper.getAsString(getInfoboxArgument(url, arg)));
    }

    private WtValue getInfoboxArgument(String url, String arg) {
        return getInfoboxArgument(getPage2Infobox(url), arg);
    }

    private WtValue getInfoboxArgument(Flux<WtTemplate> infobox, String arg) {
        var template = infobox
                .filter(t -> WikiParserHelper.templateIs(t, INFOBOX_HEADER))
                .blockFirst();

        if (template == null) {
            return WtValue.NO_VALUE;
        }
        return WikiParserHelper.getArgument(template, arg);
    }

    @Nonnull
    @Override
    public List<BulbapediaPokemonCardPage2> findAssociatedCards(@Nonnull BulbapediaPokemonCard sourceCard, @Nonnull Localization localization, String setName, boolean unnumbered) {
        var page2Infobox = getPage2Infobox(sourceCard.getLink());
        var list = PCAUtils.fluxToList(getInfoboxExtension(page2Infobox, sourceCard.getNumber(), PokemonCardHelper.otherLocalization(localization), setName, unnumbered)
                .flatMap(infoboxExtension -> {
                    BulbapediaPokemonCardPage2 card = new BulbapediaPokemonCardPage2();

                    BeanUtils.copyProperties(sourceCard, card);
                    card.setNumber(getCardNumberFromInfobox(infoboxExtension, localization));
                    card.setRarity(getRarityFromInfobox(infoboxExtension, localization));
                    return withAssociationExpansion(localization, card, handleAlternates(localization, page2Infobox, card).switchIfEmpty(Flux.just(infoboxExtension)));
                }));

        LOGGER.debug("{} association(s) found for {} ({}): [{}]", list::size, sourceCard::getName, sourceCard::getNumber,
                () -> list.stream().map(c -> c.getName() + " (" + c.getNumber() + ")").collect(Collectors.joining(", ")));
        return list;
    }

    @Nonnull
    @Override
    public List<ExtractedImageDTO> findImages(String url, Localization localization) {
        try {
            return PCAUtils.fluxToList(parsePage(url)
                    .flatMapMany(this::getTemplateWithImages)
                    .flatMapIterable(WikiParserHelper::getArguments)
                    .filter(a -> {
                        if (a.size() <= 1) {
                            return false;
                        }

                        var name = WikiParserHelper.getAsString(a);
                        
                        return !"images".equals(name) && !"reprints".equals(name) && IMAGE_ARGUMENTS.stream().anyMatch(s -> StringUtils.startsWithIgnoreCase(name, s));
                    })
                    .map(a -> WikiParserHelper.getAsString(a.getValue()))
                    .filter(StringUtils::isNotBlank)
                    .flatMap(f -> getPage(f.startsWith(FILE_PREFIX) ? f : FILE_PREFIX + f))
                    .map(Jsoup::parse)
                    .mapNotNull(d -> d.select("div.fullMedia a.internal").first().attr("href"))
                    .map(u -> u.startsWith("//") ? "https:" + u : u)
                    .map(u -> new ExtractedImageDTO(localization, "bulbapedia", u, false, null)));
        } catch (WebClientResponseException ex) {
            LOGGER.trace("Error while finding image for url {}: {}", url, ex.getMessage()); // already logged
        } catch (Exception ex) {
            LOGGER.error("Error while finding image for url {}", url, ex);
        }
        return Collections.emptyList();
    }

    @Override
    public Mono<String> getImageBase64(String url) {
        var extension = StringUtils.substringAfterLast(url, ".");

        return cacheService.getOrRequestCachedPage(url, () -> webClient.get()
                .uri(URI.create(url))
                .accept(switch (extension) {
                    case "png" -> MediaType.IMAGE_PNG;
                    case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
                    case "gif" -> MediaType.IMAGE_GIF;
                    default -> MediaType.APPLICATION_OCTET_STREAM;
                })
                .retrieve()
                .bodyToMono(byte[].class)
                .map(Base64.getEncoder()::encodeToString));
    }

    @Override
    public Mono<byte[]> getImage(String url) {
        return getImageBase64(url).map(Base64.getDecoder()::decode);
    }

    @Nonnull
    private Flux<WtTemplate> getTemplateWithImages(WtNode page) {
        if (WikiParserHelper.isNodeEmpty(page)) {
            return Flux.empty();
        }
        return Flux.fromIterable(page)
                .flatMap(node -> {
                    if (node instanceof WtTemplate template) {
                        if (WikiParserHelper.templateIs(template, TEMPLATES_WITH_IMAGE)) {
                            return Flux.just(template);
                        } else {
                            return Flux.empty();
                        }
                    }
                    return getTemplateWithImages(node);
                });
    }

    private Flux<BulbapediaPokemonCardPage2> withAssociationExpansion(Localization localization, BulbapediaPokemonCardPage2 sourceCard, final Flux<WtTemplate> infoboxExtension) {
        return findExtensionNameInInfobox(localization, infoboxExtension)
                .filter(p -> StringUtils.isNotBlank(p.getRight()))
                .map(pair -> {
                    var card = new BulbapediaPokemonCardPage2();
                    var link = pair.getRight();

                    BeanUtils.copyProperties(sourceCard, card);
                    card.setExpansionTableName(pair.getLeft());
                    if (StringUtils.isNotBlank(link)) {
                        card.setExpansionLink(link);
                    }
                    return card;
                });
    }

    @Nonnull
    private String buildWikiUrl(String name) {
        return bulbapediaWikiUrl + WIKI + name;
    }

    private String encodeUrl(String url) {
        return URLEncoder.encode(url.replace(" ", "_"), StandardCharsets.UTF_8);
    }

    private Flux<WtTemplate> handleAlternates(Localization localization, Flux<WtTemplate> page2Infobox, BulbapediaPokemonCardPage2 card) {
        var number = card.getNumber();
        var split = number.split("/");
        var total = split.length > 1 ? "/" + split[1] : "";

        if (PokemonCardHelper.isAlternate(number)) {
            card.setAlternate(true);

            return getInfoboxExtension(page2Infobox, PokemonCardHelper.removeAlternate(split[0]) + total, localization, false);
        }
        return Flux.empty();
    }

    private Flux<WtTemplate> getInfoboxExtension(String url, String number, Localization localization, String setName, boolean unnumbered) {
        return StringUtils.isNotBlank(url) ? getInfoboxExtension(getPage2Infobox(url), number, localization, setName, unnumbered) : Flux.empty();
    }

    private Flux<WtTemplate> getInfoboxExtension(Flux<WtTemplate> infobox, String number, Localization localization, boolean unnumbered) {
        return infobox.filter(t -> {
            if (!WikiParserHelper.templateIs(t, INFOBOX_EXPANSION)) {
                return false;
            }

            var n = getCardNumberFromInfobox(t, localization);

            return StringUtils.equalsIgnoreCase(n, number) || (unnumbered && StringUtils.isBlank(n));
        });
    }

    private Flux<WtTemplate> getInfoboxExtension(Flux<WtTemplate> infobox, String number, Localization localization, String setName, boolean unnumbered) {
        var alternate = PokemonCardHelper.isAlternate(number);

        return getInfoboxExtension(infobox, number, localization, unnumbered)
                .filterWhen(t -> findExtensionNameInInfobox(localization, Flux.just(t))
                        .filter(p -> alternate || StringUtils.equalsIgnoreCase(p.getLeft(), setName))
                        .count()
                        .map(c -> c > 0));
    }

    private Flux<Pair<String, String>> findExtensionNameInInfobox(Localization localization, final Flux<WtTemplate> infoboxExtension) {
        var arguments = LOCALIZATION_EXPANSION_ARGUMENTS.get(localization);

        return infoboxExtension
                .flatMap(t -> mapExtensionNameInInfobox(arguments, t))
                .filter(p -> StringUtils.isNotBlank(p.getLeft()));
    }

    @Nonnull
    private Flux<Pair<String, String>> mapExtensionNameInInfobox(List<String> arguments, WtTemplate t) {
        return Flux.fromIterable(arguments)
                .map(a -> WikiParserHelper.getArgument(t, a))
                .filter(n -> !n.isEmpty())
                .flatMap(Flux::fromIterable)
                .map(n -> {
                    var name = "";
                    var link = "";

                    if (n instanceof WtTemplate template)  {
                        if (WikiParserHelper.templateIs(template, TCG)) {
                            var pair = parseTCGTemplate(template);

                            if (StringUtils.isNotBlank(pair.getLeft()) && StringUtils.isBlank(name)) {
                                name = pair.getLeft();
                            }
                            if (StringUtils.isNotBlank(pair.getRight()) && StringUtils.isBlank(link)) {
                                link = buildWikiUrl(pair.getRight());
                            }
                        } else {
                            var parsed = parseGenericTemplate(template);

                            if (StringUtils.isNotBlank(parsed)) {
                                name = parsed;
                            }
                        }
                    } else {
                        var parsed = WikiParserHelper.getAsString(n);

                        if (StringUtils.isNotBlank(parsed)) {
                            name = parsed;
                        }
                    }
                    return Pair.of(name, link);
                });
    }

    private String getCardNumberFromInfobox(WtTemplate t, Localization localization) {
        return fixNumber(StringUtils.trimToEmpty(WikiParserHelper.getAsString(WikiParserHelper.getArgument(t, localization == Localization.JAPAN ? "jpcardno" : "cardno"))));
    }

    private String getRarityFromInfobox(WtTemplate infoboxExtension, @Nonnull Localization localization) {
        var rarityNode = WikiParserHelper.getArgument(infoboxExtension, localization == Localization.JAPAN ? "jprarity" : "rarity");

        if (!rarityNode.isEmpty()) {
            var node = rarityNode.isEmpty() ? null :rarityNode.get(0);
            var rar = "";

            if (node instanceof WtTemplate template && WikiParserHelper.templateIs(template, "rar")) {
                rar = WikiParserHelper.parseFirstArgument(template);
            } else {
                rar = WikiParserHelper.getAsString(node);
            }
            if (StringUtils.isNotBlank(rar)) {
                return fixRarity(rar);
            }
        }
        return "";
    }

    private String parseGenericTemplate(WtTemplate template) {
        return GENERIC_TEMPLATE_READERS.entrySet().stream()
                .filter(e -> WikiParserHelper.templateIs(template, e.getKey()))
                .map(e -> e.getValue().apply(this, template))
                .filter(StringUtils::isNotBlank)
                .findFirst()
                .orElseGet(() -> WikiParserHelper.getAsString(template));
    }

    private Pair<String, String> parseTCGTemplate(WtTemplate template) {
        var link = WikiParserHelper.parseFirstArgument(template);
        var name = WikiParserHelper.getAsString(WikiParserHelper.getArgument(template, 1));

        if (StringUtils.isBlank(name)) {
            name = link;
        }
        return Pair.of(name, encodeUrl(link) + "_(TCG)");
    }

    private String parseFirstPrefixedByPokemonPTemplate(WtTemplate template) {
        return "Pokémon " + WikiParserHelper.parseFirstArgument(template);
    }

    private String parse2Or1Template(WtTemplate template) {
        var arg0 = WikiParserHelper.parseFirstArgument(template);
        var arg1 = WikiParserHelper.getAsString(WikiParserHelper.getArgument(template, 1));

        return StringUtils.isNotBlank(arg1) ? arg1 : arg0;
    }

    private Pair<String, String> parseOBPTemplate(WtTemplate template) {
        return Pair.of(WikiParserHelper.parseFirstArgument(template), WikiParserHelper.getAsString(WikiParserHelper.getArgument(template, 1)));
    }

    private String parseEnergyTemplate(WtTemplate template) {
        var arg0 = WikiParserHelper.parseFirstArgument(template);

        return StringUtils.isNotBlank(arg0) ? arg0 : "Colorless";
    }

    @Nonnull
    private Flux<WtTemplate> getPage2Infobox(String url) {
        return parsePage(url)
                .flatMapIterable(this::getInfobox)
                .cache();
    }

    @Nonnull
    private List<WtTemplate> getInfobox(WtNode page) {
        if (WikiParserHelper.isNodeEmpty(page)) {
            return Collections.emptyList();
        }

        var index = -1;

        for (int i = 0; i < page.size(); i++) {
            var node = page.get(i);
            if (node instanceof WtTemplate template) {
                if (WikiParserHelper.templateIs(template, INFOBOX_HEADER)) {
                    index = i;
                    break;
                }
            } else {
                var entries = getInfobox(node);

                if (!entries.isEmpty()) {
                    return entries;
                }
            }
        }

        if (index == -1) {
            return Collections.emptyList();
        }

        return getInfoboxTemplates(page, index);
    }

    private ArrayList<WtTemplate> getInfoboxTemplates(WtNode page, int index) {
        var value = new ArrayList<WtTemplate>(page.size());

        for (int i = index; i < page.size(); i++) {
            if (page.get(i) instanceof WtTemplate template) {
                if (WikiParserHelper.templateIs(template, INFOBOX_RELEASE_INFO)) {
                    for (var node2 : WikiParserHelper.getArgument(template, "releases")) {
                        if (node2 instanceof WtTemplate template2 && WikiParserHelper.templateIs(template2, INFOBOX_TEMPLATES)) {
                            value.add(template2);
                        }
                    }
                }
                if (WikiParserHelper.templateIs(template, INFOBOX_TEMPLATES)) {
                    value.add(template);
                } else if (WikiParserHelper.templateIs(template, INFOBOX_FOOTER)) {
                    value.add(template);
                    break;
                }
            }
        }
        return value;
    }

    private Mono<String> getPage(String url) {
        if (StringUtils.isBlank(url)) {
            return Mono.empty();
        }

        var sanitizedUrl = sanitizeUrl(url);

        if (sanitizedUrl.startsWith(FILE_PREFIX)) {
            var imageUri = bulbapediaWikiUrl + WIKI + sanitizedUrl;

            return cacheService.getOrRequestCachedPage(imageUri, () -> webClient.get()
                    .uri(URI.create(imageUri))
                    .retrieve()
                    .bodyToMono(String.class));
        }

        var uri = "page/" + sanitizedUrl;

        return cacheService.getOrRequestCachedPage(urlBulbapediaApi + uri, () -> webClient.get()
                .uri(b -> b.path(uri).build())
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    if (sanitizedUrl.contains("/")) {
                        return webClient.get()
                                .uri(URI.create(bulbapediaWikiUrl + "/w/index.php?title=" + sanitizedUrl + "&action=edit"))
                                .retrieve()
                                .bodyToMono(String.class)
                                .map(Jsoup::parse)
                                .mapNotNull(d -> d.select("textarea#wpTextbox1").first())
                                .map(Element::text)
                                .filter(StringUtils::isNotBlank)
                                .flatMap(s -> {
                                    try {
                                        return Mono.just(objectMapper.writeValueAsString(new WikiPage(sanitizedUrl, sanitizedUrl, s)));
                                    } catch (JsonProcessingException ex) {
                                        return Mono.error(new BulbapediaException(e));
                                    }
                                });
                    }
                    return Mono.empty();
                }).retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(5))))
                .onErrorResume(e -> Mono.empty());
    }

    private Mono<WtNode> parsePage(String url) {
        if (StringUtils.isBlank(url)) {
            return Mono.empty();
        }

        return cacheService.getCached("bulbapedia-parsed-page", url, () -> getPage(url)
                .flatMap(s -> {
                    try {
                        var page = objectMapper.readValue(s, WikiPage.class);
                        var title = page.title();
                        var source = cleanSource(page.source());

                        if (StringUtils.containsIgnoreCase(source, "#REDIRECT")) {
                            var redirect = PCAUtils.substringBetween(source, "[[", "]]");

                            return parsePage(redirect);
                        }
                        return Mono.just(this.parser.parseArticle(source, title));
                    } catch (IOException | ParseException | ClassCastException e) {
                        return Mono.error(new BulbapediaException(e));
                    }
                })
                .onErrorResume(e -> Mono.empty()));
    }

    private String cleanSource(String source) {
        return StringUtils.replace(source, "\n\n", "\n");
    }

    private String sanitizeUrl(String url) {
        return URLDecoder.decode(StringUtils.removeStart(url
                .replace(bulbapediaWikiUrl + WIKI, "")
                .replace(WIKI, ""), "\\/"), StandardCharsets.UTF_8)
                .replaceAll("\\s", "_");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record WikiPage(
            String title,
            String key,
            String source
    ) {}
}
