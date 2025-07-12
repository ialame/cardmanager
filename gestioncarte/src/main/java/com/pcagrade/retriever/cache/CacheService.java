package com.pcagrade.retriever.cache;

import jakarta.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@Service
public class CacheService {

	private static final Logger LOGGER = LogManager.getLogger(CacheService.class);

	@Autowired(required = false)
	private List<IEvictable> evictables;
	
	@Autowired
	private CacheManager cacheManager;

	private String pageCachePath;
	private Duration cacheDuration = Duration.ofDays(14);

	@Autowired
	public void setCacheDuration(@Value("${retriever.cache.page-cache.duration}") String cacheDuration) {
		this.cacheDuration = Duration.parse(cacheDuration);
	}

	@Autowired
	public void setPageCachePath(@Value("${retriever.cache.page-cache.path}") String pageCachePath) {
		this.pageCachePath = new File(pageCachePath).getAbsolutePath();
	}

	@Scheduled(cron = "${retriever.schedule.chache-evict}")
	public void evictAllCaches() {
		LOGGER.info("Evicting all caches");
		cacheManager.getCacheNames().forEach(cacheName -> cacheManager.getCache(cacheName).clear());
		if (CollectionUtils.isNotEmpty(evictables)) {
			evictables.forEach(IEvictable::evict);
		}
	}

	public synchronized <T> T getCached(String cacheName, Supplier<T> supplier) {
		return getCached(cacheName, cacheName, supplier);
	}

	public synchronized <T> T getCached(String cacheName, Object key, Supplier<T> supplier) {
		if (key == null) {
			return supplier.get();
		}

		var cache = cacheManager.getCache(cacheName);

		if (cache == null) {
			LOGGER.warn("Cache {} not found", cacheName);
			return supplier.get();
		}
		return cache.get(key, supplier::get);
	}

	@Cacheable(cacheNames = "page-cache", key = "#url")
	public synchronized Mono<String> getOrRequestCachedPage(String url, Supplier<Mono<String>> supplier) {
		return Mono.defer(() -> this.doGetOrRequestCachedPage(url, supplier)).cache();
	}

	private synchronized Mono<String> doGetOrRequestCachedPage(String url, Supplier<Mono<String>> supplier) {
		var encoded = URLEncoder.encode(url, StandardCharsets.UTF_8);
		var file = getFile(encoded + ".txt");
		var exists = file.exists();
		var expired = isExpired(file);

		if (exists && !expired) {
			LOGGER.debug("Cache hit for {}", url);
			return readCache(file);
		}

		LOGGER.debug("Requesting page {}", url);
		return Mono.defer(supplier)
				.flatMap(s -> {
					if (StringUtils.isBlank(s)) {
						return Mono.error(new PageCacheException("Page is empty"));
					}
					var folder = file.getParentFile();

					if (!folder.exists() && !folder.mkdirs()) {
						return Mono.error(new PageCacheException("Failed to create folder " + folder.getAbsolutePath()));
					}
					try {
						Files.writeString(file.toPath(), s);
					} catch (IOException e) {
						return Mono.error(new PageCacheException("Failed to write page to cache", e));
					}
					return Mono.just(s);
				})
				.onErrorResume(e -> {
					if (exists) {
						return readCache(file);
					}
					return Mono.error(e);
				})
				.doOnError(e -> {
					if (e instanceof WebClientResponseException requestException) {
						LOGGER.warn("Failed to request page {}: {}", url, requestException.getMessage());
					} else {
						LOGGER.error("Failed to request page {}", url, e);
					}
				});
	}


	private boolean isExpired(File file) {
		if (!file.exists()) {
			return true;
		}
		try {
			return Instant.now().compareTo(Files.getLastModifiedTime(file.toPath()).toInstant().plus(cacheDuration)) > 0;
		} catch (IOException e) {
			LOGGER.error("Failed to check if file is expired", e);
			return false;
		}
	}

	@Nonnull
	private Mono<String> readCache(File file) {
		return Mono.using(() -> Files.readString(file.toPath()), Mono::just, s -> {});
	}

	public List<String> getCachedPages() {
		var list = new File(pageCachePath).list();

		if (list == null) {
			return Collections.emptyList();
		}
		return Arrays.stream(list)
				.map(file -> file.substring(0, file.length() - 4))
				.toList();
	}

	public void removeCachedPage(String url) {
		var encoded = URLEncoder.encode(url, StandardCharsets.UTF_8);
		var file = getFile(encoded + ".txt");

		try {
			if (file.exists()) {
				Files.delete(file.toPath());
			}
		} catch (IOException e) {
			LOGGER.error("Failed to delete cached page", e);
		}
		LOGGER.debug("Removed cached page {}", url);
	}

	@Nonnull
	private File getFile(String path) {
		var file = new File(pageCachePath, path);

		checkFileLocation(file);
		return file;
	}


	private void checkFileLocation(File file) {
		var path = file.getAbsolutePath();

		if (!path.startsWith(pageCachePath)) {
			throw new SecurityException("Cache file: " + path + " is not within the configured path");
		}
	}

	public void evict(String name) {
		if (StringUtils.isBlank(name)) {
			return;
		}

		var cache = cacheManager.getCache(name);

		if (cache != null) {
			cache.clear();
		}
	}
}
