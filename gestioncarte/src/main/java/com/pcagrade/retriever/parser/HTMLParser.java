package com.pcagrade.retriever.parser;

import com.pcagrade.retriever.cache.CacheService;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Base64;

@Component
public class HTMLParser implements IHTMLParser {

	@Autowired
	private CacheService cacheService;

	private final WebClient webClient;

	public HTMLParser(@Value("${retriever.web-client.max-in-memory-size}") String maxInMemorySize) {
		this.webClient = WebClient.builder()
				.exchangeStrategies(RetrieverHTTPHelper.createExchangeStrategies(maxInMemorySize))
				.clientConnector(RetrieverHTTPHelper.createReactorClientHttpConnector("html-parser-connection-provider", 10))
				.build();
	}

	@Override
	@Nonnull
	public Mono<Document> getMono(String url) {
		return cacheService.getOrRequestCachedPage(url, () -> webClient.get()
						.uri(url)
						.retrieve()
						.bodyToMono(String.class))
				.map(Jsoup::parse);
	}

	@Override
	@Nonnull
	public Mono<byte[]> getImage(String url) {
		return getImageBase64(url).map(Base64.getDecoder()::decode);
	}

	@Override
	@Nonnull
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


}
