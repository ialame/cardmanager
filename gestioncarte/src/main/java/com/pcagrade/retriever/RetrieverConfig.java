package com.pcagrade.retriever;

import io.micrometer.core.instrument.MeterRegistry;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.SimilarityScore;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class RetrieverConfig {

	@Bean
	public SimilarityScore<Double> similarityScore() {
		var similarity = new LevenshteinDistance();

		return (cs1, cs2) -> {
			var s1 = StringUtils.trimToEmpty(cs1 != null ? cs1.toString() : "").toLowerCase();
			var s2 = StringUtils.trimToEmpty(cs2 != null ? cs2.toString() : "").toLowerCase();
			var length = Math.max(s1.length(), s2.length());

			if (length == 0) {
				return 1d;
			}

			var score = similarity.apply(s1, s2);

			if (score == null) {
				return 0d;
			}
			return 1 - (score / (double) length);
		};
	}

	@Bean
	@Order(1)
	public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
		return registry -> registry.config().commonTags("application", "Professional Card Retriever");
	}

}
