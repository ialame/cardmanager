package com.pcagrade.retriever.card.tag;

import com.pcagrade.retriever.annotation.RetrieverTest;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(CardTagTestConfig.class)
class CardTagMapperShould {

	@Autowired
	CardTagRepository cardTagRepository;

	@Autowired
	CardTagMapper cardTagMapper;


	@ParameterizedTest
	@MethodSource("provideValidTags")
	@Order(1)
	void mapElement(CardTag cardTag) {
		CardTagDTO dto = cardTagMapper.mapToDto(cardTag);

		assertThat(dto).usingRecursiveComparison()
				.ignoringFields("id", "translations")
				.isEqualTo(cardTag);
	}

	private Stream<Arguments> provideValidTags() {
		return CardTagTestProvider.LIST.stream().map(Arguments::of);
	}

	@Test
	@Order(2)
	void mapAllElement() {
		List<CardTag> cardTags = cardTagRepository.findAll();
		List<CardTagDTO> dtos = cardTagMapper.mapToDto(cardTags);

		assertThat(dtos).usingRecursiveComparison()
				.ignoringFields("id", "translations")
				.isEqualTo(cardTags);
	}

}
