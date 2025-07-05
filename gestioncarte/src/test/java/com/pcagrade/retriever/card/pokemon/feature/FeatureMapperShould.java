package com.pcagrade.retriever.card.pokemon.feature;

import com.pcagrade.retriever.annotation.RetrieverTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(FeatureTestConfig.class)
class FeatureMapperShould {

	@Autowired
	FeatureMapper featureMapper;

	@ParameterizedTest
	@MethodSource("provideValidFeatures")
	void mapElement(Feature feature) {
		FeatureDTO dto = featureMapper.mapToDto(feature);

		assertThat(dto).usingRecursiveComparison()
				.ignoringFields("translations", "id")
				.isEqualTo(feature);
	}

	@Test
	void mapFromDTO() {
		var dto = FeatureTestProvider.exDTO();
		var feature = featureMapper.mapFromDto(dto);

		assertThat(feature).usingRecursiveComparison()
				.ignoringFields("translations", "cards", "name", "ulid")
				.isEqualTo(dto);
	}

	private Stream<Arguments> provideValidFeatures() {
		return Stream.of(Arguments.of(FeatureTestProvider.mega(), FeatureTestProvider.ex()));
	}

}
