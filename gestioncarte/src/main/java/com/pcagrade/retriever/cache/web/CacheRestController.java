package com.pcagrade.retriever.cache.web;

import com.pcagrade.retriever.cache.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/caches")
public class CacheRestController {
	
	@Autowired
	private CacheService cacheService;

	@DeleteMapping
	public void evictAllCaches() {
		cacheService.evictAllCaches();
	}

	@GetMapping("/pages")
	public List<String> getCachedPages() {
		return cacheService.getCachedPages();
	}

	@DeleteMapping("/pages")
	public void removeCachedPage(@RequestBody List<String> urls) {
		urls.forEach(cacheService::removeCachedPage);
	}
}
