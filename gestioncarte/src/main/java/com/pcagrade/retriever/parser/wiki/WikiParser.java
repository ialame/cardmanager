package com.pcagrade.retriever.parser.wiki;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcagrade.mason.json.web.JsonWebHelper;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.cache.CacheService;
import com.pcagrade.retriever.parser.RetrieverHTTPHelper;
import com.pcagrade.retriever.parser.wiki.result.WikiCategoryMember;
import com.pcagrade.retriever.parser.wiki.result.WikiCategoryResult;
import com.pcagrade.retriever.parser.wiki.result.WikiPage;
import com.pcagrade.retriever.parser.wiki.result.WikiPageResult;
import com.pcagrade.retriever.parser.wiki.result.WikiPageRevision;
import com.pcagrade.retriever.parser.wiki.result.WikiPageSlot;
import com.pcagrade.retriever.parser.wiki.result.ask.WikiAskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import org.sweble.wikitext.parser.nodes.WtNode;
import org.sweble.wikitext.parser.utils.NonExpandingParser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import xtc.parser.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.function.BiFunction;

public class WikiParser {

    private static final ParameterizedTypeReference<WikiPageResult<WikiPageRevision>> SLOT_TYPE = new ParameterizedTypeReference<>() { };
    private static final ParameterizedTypeReference<WikiPageResult<WikiPageSlot>> SLOTLESS_TYPE = new ParameterizedTypeReference<>() { };

    private final ObjectMapper objectMapper;
    private final CacheService cacheService;
    private final WebClient webClient;
    private final NonExpandingParser parser;

    private boolean useSlots;

    public WikiParser(ObjectMapper objectMapper, CacheService cacheService, String url) {
        this(objectMapper, cacheService, WebClient.builder()
                .baseUrl(url)
                .exchangeStrategies(JsonWebHelper.createExchangeStrategies(objectMapper))
                .clientConnector(RetrieverHTTPHelper.createReactorClientHttpConnector("wiki-api-connection-provider", 2))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build());
        useSlots = true;
    }

    public WikiParser(ObjectMapper objectMapper, CacheService cacheService, WebClient webClient) {
        this.objectMapper = objectMapper;
        this.cacheService = cacheService;
        this.webClient = webClient;
        this.parser = new NonExpandingParser();
    }

    public Flux<String> listCategory(String url) {
        if (StringUtils.isBlank(url)) {
            return Flux.empty();
        }

        return cacheService.getCached("wiki-parsed-page", url, () -> getCategoryPage(url)
                .flatMap(s -> {
                    try {
                        return Mono.just(objectMapper.readValue(s, WikiCategoryResult.class));
                    } catch (IOException | ClassCastException e) {
                        return Mono.error(new WikiParsingException(e));
                    }
                })
                .flatMapIterable(r -> r.query().categoryMembers())
                .map(WikiCategoryMember::title));
    }

    private Mono<String> getCategoryPage(String url) {
        return getPage(url, (s, b) -> b.queryParam("action", "query")
                .queryParam("format", "json")
                .queryParam("list", "categorymembers")
                .queryParam("cmtitle", s)
                .queryParam("cmlimit", 100)
                .build());
    }

    public Mono<WtNode> parsePage(String url) {
        if (StringUtils.isBlank(url)) {
            return Mono.empty();
        }

        return getWikiPage(url)
                .flatMap(page -> {
                    try {
                        return Mono.just(this.parser.parseArticle(page.revisions().isEmpty() ? null :page.revisions().get(0).content(), page.title()));
                    } catch (IOException | ParseException | ClassCastException e) {
                        return Mono.error(new WikiParsingException(e));
                    }
                })
                .onErrorResume(e -> Mono.empty());
    }

    public Mono<String> getPageTitle(String url) {
        if (StringUtils.isBlank(url)) {
            return Mono.empty();
        }

        return getWikiPage(url)
                .map(WikiPage::title)
                .onErrorResume(e -> Mono.empty());
    }

    public Mono<WikiAskQuery> ask(String query) {
        return getPage(query, (s, b) -> b.queryParam("action", "ask")
                .queryParam("format", "json")
                .queryParam("query", query)
                .build())
                .flatMap(s -> {
                    try {
                        return Mono.just(objectMapper.readValue(s, WikiAskQuery.class));
                    } catch (IOException e) {
                        return Mono.error(new WikiParsingException(e));
                    }
                })
                .onErrorResume(e -> Mono.empty());
    }

    private Mono<WikiPage<?>> getWikiPage(String url) {
        if (StringUtils.isBlank(url)) {
            return Mono.empty();
        }

        return cacheService.getCached("wiki-parsed-page", url, () -> getPage(url)
                .flatMap(s -> {
                    try {
                        return Mono.just((WikiPageResult<?>) objectMapper.readValue(s, PCAUtils.getJavaType(objectMapper, useSlots ? SLOT_TYPE : SLOTLESS_TYPE)));
                    } catch (IOException | ClassCastException e) {
                        return Mono.error(new WikiParsingException(e));
                    }
                })
                .flatMapIterable(r -> r.query().pages().values())
                .flatMap(page -> {
                    try {
                        var source =page.revisions().isEmpty() ? null :page.revisions().get(0).content();

                        if (StringUtils.containsIgnoreCase(source, "#REDIRECT")) {
                            var redirect = PCAUtils.substringBetween(source, "[[", "]]");

                            return getWikiPage(redirect);
                        }
                        return Mono.just(page);
                    } catch (ClassCastException e) {
                        return Mono.error(new WikiParsingException(e));
                    }
                })
                .onErrorResume(e -> Mono.empty())
                .next());
    }

    private Mono<String> getPage(String url) {
        return getPage(url, (s, b) -> {
            b.queryParam("action", "query")
                    .queryParam("format", "json")
                    .queryParam("prop", "revisions")
                    .queryParam("rvprop", "content")
                    .queryParam("titles", s);
            if (useSlots) {
                b.queryParam("rvslots", "main");
            }
            return b.build();
        });
    }

    private Mono<String> getPage(String url, BiFunction<String, UriBuilder, URI> builder) {
        if (StringUtils.isBlank(url)) {
            return Mono.empty();
        }

        var title = extractTitleFromUrl(url);

        return cacheService.getOrRequestCachedPage(title, () -> webClient.get()
                        .uri(b -> builder.apply(title, b))
                        .retrieve()
                        .bodyToMono(String.class)
                        .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(5))))
                .onErrorResume(e -> Mono.empty());
    }

    private String extractTitleFromUrl(String url) {
        return URLDecoder.decode(StringUtils.containsIgnoreCase(url, "wiki/") ? StringUtils.substringAfterLast(url, "wiki/") : url, StandardCharsets.UTF_8)
                .replace("''", "")
                .replaceAll("\\s", "_");
    }

    public boolean isUseSlots() {
        return useSlots;
    }

    public void setUseSlots(boolean useSlots) {
        this.useSlots = useSlots;
    }
}
