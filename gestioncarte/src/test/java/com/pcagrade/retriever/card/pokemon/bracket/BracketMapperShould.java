package com.pcagrade.retriever.card.pokemon.bracket;

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

@RetrieverTest(BracketTestConfig.class)
class BracketMapperShould {

	@Autowired
	BracketRepository bracketRepository;

	@Autowired
	BracketMapper bracketMapper;


	@ParameterizedTest
	@MethodSource("provideValidBrackets")
	@Order(1)
	void mapElement(Bracket bracket) {
		BracketDTO dto = bracketMapper.mapToDto(bracket);

		assertThat(dto).usingRecursiveComparison()
				.ignoringFields("localization", "id", "translations")
				.isEqualTo(bracket);
	}

	private Stream<Arguments> provideValidBrackets() {
		return BracketTestProvider.provideValidBrackets().stream().map(Arguments::of);
	}

	@Test
	@Order(2)
	void mapAllElement() {
		List<Bracket> brackets = bracketRepository.findAll();
		List<BracketDTO> dtos = bracketMapper.mapToDto(brackets);

		assertThat(dtos).usingRecursiveComparison()
				.ignoringFields("localization", "id", "translations")
				.isEqualTo(brackets);
	}

}
