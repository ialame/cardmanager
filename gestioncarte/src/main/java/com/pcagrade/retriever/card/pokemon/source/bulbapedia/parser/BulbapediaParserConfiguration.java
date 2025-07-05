package com.pcagrade.retriever.card.pokemon.source.bulbapedia.parser;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BulbapediaParserConfiguration {

	@Bean
	public IBulbapediaParser bulbapediaParser(@Value("${retriever.web-client.max-in-memory-size}") String maxInMemorySize, @Value("${bulbapedia.api.url}") String url) {
		return new BulbapediaApiParser(maxInMemorySize, url);
	}
}
