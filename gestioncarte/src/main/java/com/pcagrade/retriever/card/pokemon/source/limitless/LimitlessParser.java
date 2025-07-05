package com.pcagrade.retriever.card.pokemon.source.limitless;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.image.ExtractedImageDTO;
import com.pcagrade.retriever.parser.IHTMLParser;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.pcagrade.retriever.PCAUtils.toMap;

@Component
public class LimitlessParser {

    @Autowired
    private IHTMLParser htmlParser;

    @Value("${limitless-com.url}")
    private String limitlessUrl;

    private static final Pattern NUMBER_PATTERN = Pattern.compile("#(\\d+)");

    private static final String BASE_URL = "https://limitlesstcg.com";
    private static final String POKEMON_TCG_API_BASE_URL = "https://api.pokemontcg.io/v2/cards?q=set.id:";
    private static final String POKEMON_TCG_SETS_API_URL = "https://api.pokemontcg.io/v2/sets";
    private static final String LIMITLESS_SETS_URL = "https://limitlesstcg.com/cards";
    // Cache des sets (nom -> [code, nombre de cartes])
    public Map<String, String> setNameToCode = new HashMap<>();
    public Map<String, String> setNameToSetId = new HashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();


    @PostConstruct
    public void initializeSetMappings() {
        Map<String, String> sets = scrapeSetsFromLimitless();
        for (String k : sets.keySet()) {
            setNameToCode.put(sets.get(k), k);
        }

        Map<String, String> setIdMap = fetchSetIdsFromPokemonTcgApi();
        setNameToSetId.putAll(setIdMap);
    }

    public Map<String, String> scrapeSetsFromLimitless() {
        return get(LIMITLESS_SETS_URL)
                .flatMapIterable(doc -> doc.select("table.data-table.sets-table > tbody > tr"))
                .flatMap(e -> {
                    // Extraction du lien
                    Element link = e.selectFirst("td:first-child a");
                    if (link == null) {
                        return Mono.empty(); // ignorer les lignes sans lien
                    }

                    String href = link.attr("href");
                    if (!StringUtils.hasText(href)) {
                        return Mono.empty();
                    }

                    String code = href.substring(href.lastIndexOf("/") + 1);

                    // Extraction du nom du set (prendre le texte visible)
                    String name = link.ownText().trim();
                    if (!StringUtils.hasText(name)) {
                        return Mono.empty();
                    }

                    return Mono.just(Map.entry(code, name));
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> existing // En cas de doublon, garder le premier
                ))
                .blockOptional()
                .orElse(Collections.emptyMap());
    }

    private Map<String, String> fetchSetIdsFromPokemonTcgApi() {
        Map<String, String> setIdMap = new HashMap<>();
        int maxRetries = 3;
        int retryDelayMs = 2000;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                System.out.println("Fetching sets from Pokémon TCG API (attempt " + attempt + "): " + POKEMON_TCG_SETS_API_URL);
                ResponseEntity<String> response = restTemplate.exchange(POKEMON_TCG_SETS_API_URL, HttpMethod.GET, HttpEntity.EMPTY, String.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    JsonNode root = objectMapper.readTree(response.getBody());
                    JsonNode sets = root.path("data");
                    for (JsonNode set : sets) {
                        String name = set.path("name").asText();
                        String id = set.path("id").asText();
                        setIdMap.put(name, id);
                        System.out.println("Mapped set: name=" + name + ", set.id=" + id);
                    }
                    break;
                } else {
                    System.err.println("API request failed with status: " + response.getStatusCode());
                    if (attempt == maxRetries) {
                        System.err.println("Max retries reached, giving up");
                    } else {
                        System.out.println("Retrying in " + retryDelayMs + "ms...");
                        Thread.sleep(retryDelayMs);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error fetching sets from Pokémon TCG API: " + e.getMessage());
                if (attempt == maxRetries) {
                    System.err.println("Max retries reached, giving up");
                } else {
                    System.out.println("Retrying in " + retryDelayMs + "ms...");
                    try {
                        Thread.sleep(retryDelayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        return setIdMap;
    }


    public Map<String, ExtractedImageDTO> getImages(String setName, Localization localization) {
        return get(getUrl(setName,localization))
                .flatMapIterable(b -> b.select("div.card-search-grid a"))
                .flatMap(e -> {
                    var name = e.select("a").attr("href");
                    var image = e.select("img").attr("src").replace("SM", "LG");
                    //var match = NUMBER_PATTERN.matcher(name);
                    String number = name.split("/")[name.split("/").length - 1];
                    return getImage(image, localization)
                            .map(i -> Map.entry(number, i));
                })
                .collect(toMap())
                .blockOptional()
                .orElse(Collections.emptyMap());
    }



    private Mono<ExtractedImageDTO> getImage(String url, Localization localization) {
        return Mono.just(new ExtractedImageDTO(localization, "limitless", url, false, null));
    }

    public byte[] getImage(String url) {
        return htmlParser.getImage(url).block();
    }


    public String getUrl(String setCode, Localization localization) {
        return limitlessUrl + "cards/" + (Localization.USA==localization?"":(localization.getCode()+"/"))+ setCode;
    }

    @Nonnull
    private Mono<Element> get(String url) {
        return htmlParser.getMono(htmlParser.processUrl(this.limitlessUrl, url))
                .map(Document::body);
    }
}
