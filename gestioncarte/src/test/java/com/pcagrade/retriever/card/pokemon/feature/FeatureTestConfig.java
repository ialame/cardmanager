package com.pcagrade.retriever.card.pokemon.feature;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.feature.translation.FeatureTranslationMapper;
import com.pcagrade.retriever.card.pokemon.feature.translation.FeatureTranslationMapperImpl;
import com.pcagrade.retriever.card.pokemon.feature.translation.pattern.FeatureTranslationPatternTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import(FeatureTranslationPatternTestConfig.class)
public class FeatureTestConfig {

	@Bean
	public FeatureMapper featureMapper() {
		return new FeatureMapperImpl();
	}

	@Bean
	public FeatureTranslationMapper featureTranslationMapper() {
		return new FeatureTranslationMapperImpl();
	}

	@Bean
	public FeatureService featureService() {
		return new FeatureService();
	}

}
