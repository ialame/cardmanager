package com.pcagrade.retriever.card.yugioh.source.ygoprodeck;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcagrade.mason.json.web.JsonWebHelper;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.cache.CacheService;
import com.pcagrade.retriever.parser.RetrieverHTTPHelper;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class YgoProDeckParser {

    private final WebClient webClient;
    private final RateLimiter rateLimiter;

    @Autowired
    private CacheService cacheService;

    public YgoProDeckParser(@Value("${ygoprodeck-com.url}")String url, @Autowired ObjectMapper objectMapper) {
        this.webClient = WebClient.builder()
                .baseUrl(url)
                .exchangeStrategies(JsonWebHelper.createExchangeStrategies(objectMapper))
                .clientConnector(RetrieverHTTPHelper.createReactorClientHttpConnector("ygoprodeck-com-connection-provider", 2))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.rateLimiter = RateLimiter.of("ygoprodeck-com-rate-limiter", RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .limitForPeriod(10)
                .timeoutDuration(Duration.ofSeconds(20))
                .build());

    }

    public List<YgoProDeckSet> getSets() {
        return cacheService.getCached("YgoProDeckParserSetList", () -> webClient.get()
                .uri("cardsets.php")
                .retrieve()
                .bodyToFlux(YgoProDeckSet.class)
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .filter(s -> StringUtils.isNotBlank(s.setCode()) && StringUtils.isNotBlank(s.setName()) && s.cardCount() > 0)
                .collectList()
                .block());
    }

    public List<YgoProDeckCard> getCards(String setCode, Localization localization) {
        return getSets().stream()
                .filter(set -> set.setCode().equals(setCode))
                .findFirst()
                .map(set -> getCardsBySetName(set.setName(), localization))
                .orElse(Collections.emptyList());
    }

    private List<YgoProDeckCard> getCardsBySetName(String setName, Localization localization) {
        return webClient.get()
                .uri(b -> b.path("cardinfo.php")
                        .queryParam("cardset", StringUtils.lowerCase(setName))
                        .queryParamIfPresent("language", getLocalizationCode(localization))
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<DataWrapper<List<YgoProDeckCard>>>() {})
                .flatMapIterable(DataWrapper::data)
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .collectList()
                .block();
    }

    @Nonnull
    private static Optional<?> getLocalizationCode(Localization localization) {
        return localization == Localization.USA ? Optional.empty() : Optional.of(localization.getCode());
    }

    private record DataWrapper<T>(T data) { }

}
