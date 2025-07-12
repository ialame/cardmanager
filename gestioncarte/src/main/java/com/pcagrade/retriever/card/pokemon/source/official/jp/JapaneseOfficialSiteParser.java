package com.pcagrade.retriever.card.pokemon.source.official.jp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.cache.CacheService;
import com.pcagrade.retriever.card.pokemon.PokemonCardHelper;
import com.pcagrade.retriever.card.pokemon.source.official.jp.source.JapaneseOfficialSiteSourceService;
import com.pcagrade.retriever.parser.IHTMLParser;
import com.pcagrade.retriever.parser.RetrieverHTTPHelper;
import jakarta.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Component
@CacheConfig(cacheNames={"japaneseOfficialSiteParser"})
public class JapaneseOfficialSiteParser {

    private static final Logger LOGGER = LogManager.getLogger(JapaneseOfficialSiteParser.class);

    @Autowired
    private IHTMLParser htmlParser;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JapaneseOfficialSiteSourceService japaneseOfficialSiteSourceService;

    private final WebClient client;
    private final String pokemonCardComUrl;

    public JapaneseOfficialSiteParser(@Value("${pokemon-card-com.url}")String url, @Value("${retriever.web-client.max-in-memory-size}") String maxInMemorySize) {
        this.pokemonCardComUrl = url;
        client = WebClient.builder()
                .baseUrl(url)
                .exchangeStrategies(RetrieverHTTPHelper.createExchangeStrategies(maxInMemorySize))
                .clientConnector(RetrieverHTTPHelper.createReactorClientHttpConnector("japanese-official-site-connection-provider", 10))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }

    @Cacheable
    public Map<String, JapaneseOfficialSiteCard> parse(Ulid setId) {
        return japaneseOfficialSiteSourceService.getPgs(setId).stream()
                .<Map.Entry<String, String>>mapMulti((p, downstream) -> getCardList(p).entrySet().forEach(downstream))
                .map(e -> getCard(e.getKey(), e.getValue()))
                .filter(Objects::nonNull)
                .collect(PCAUtils.toMap(JapaneseOfficialSiteCard::number, Function.identity()));
    }

    private JapaneseOfficialSiteCard getCard(String id, String rawName) {
        try {
            var url = getUrl(id);
            var page = get(url);
            Element numberElement = page
                    .select("div.subtext.Text-fjalla")
                    .first();
            Element artistElement = page
                    .select("div.author>a")
                    .first();

            var namePair = PokemonCardHelper.extractTrainer(rawName);
            var name = URLDecoder.decode(namePair.getLeft(), StandardCharsets.UTF_8);
            var trainer = URLDecoder.decode(namePair.getRight(), StandardCharsets.UTF_8);
            var number = numberElement != null ? numberElement.text().replaceAll("\\s+", "") : "";
            var artist = artistElement != null ? artistElement.text() : "";

            return new JapaneseOfficialSiteCard(id, url, number, name, artist, trainer);
        } catch (WebClientResponseException e) {
            LOGGER.warn("Failed to get number for id: {}: {}", id, e.getMessage());
        } catch (IOException e) {
            LOGGER.error(() -> "Failed to get number for id: " + id, e);
        }
        return null;
    }

    private Map<String, String> getCardList(String pg) {
        if (StringUtils.isBlank(pg)) {
            return Collections.emptyMap();
        }

        var result = getCardList(pg, 1);

        if (result == null) {
            return Collections.emptyMap();
        }

        var map = new HashMap<String, String>();

        result.put(map);
        for (int i = 2; i <= result.maxPage(); i++) {
            var r = getCardList(pg, i);

            if (r != null) {
                r.put(map);
            }
        }
        return map;
    }

    private Result getCardList(String pg, int page) {
        var body = "keyword=&se_ta=&regulation_sidebar_form=XY&pg=" + pg + "&illust=&sm_and_keyword=true&page=" + page;

        return cacheService.getOrRequestCachedPage("official-site-jp/" + body, () -> client.post().uri("/card-search/resultAPI.php")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class))
                .flatMap(s -> {
                    try {
                        return Mono.just(objectMapper.readValue(s, Result.class));
                    } catch (JsonProcessingException e) {
                        return Mono.error(e);
                    }
                }).block();
    }

    private String getUrl(String id) {
        return htmlParser.processUrl(this.pokemonCardComUrl,  "card-search/details.php/card/" + id + "/regu/all");
    }

    @Nonnull
    private Element get(String url) throws IOException {
        return htmlParser.get(htmlParser.processUrl(this.pokemonCardComUrl, url)).body();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Result(
            int thisPage,
            int maxPage,
            List<Card> cardList
    ) {
        private void put(Map<String, String> map) {
            if (CollectionUtils.isNotEmpty(cardList)) {
                cardList.forEach(card -> map.put(card.cardID(), card.cardNameViewText()));
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        private record Card(
                String cardID,
                String cardNameViewText
        ) {}
    }
}
