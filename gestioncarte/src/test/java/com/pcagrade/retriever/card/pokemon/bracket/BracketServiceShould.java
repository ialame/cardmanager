package com.pcagrade.retriever.card.pokemon.bracket;

import com.pcagrade.retriever.annotation.RetrieverTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(BracketTestConfig.class)
class BracketServiceShould {

	@Autowired
	BracketRepository bracketRepository;
	@Autowired
	BracketService bracketService;

	@ParameterizedTest
	@MethodSource("provideValidBrackets")
	void findByName(Bracket sourceBracket) {
		BracketDTO bracket = bracketService.findByName(sourceBracket.getName());

		assertThat(bracket).isNotNull().usingRecursiveComparison()
				.ignoringFields("localization", "id", "translations")
				.isEqualTo(sourceBracket);
	}

	@ParameterizedTest
	@MethodSource("provideValidBrackets")
	void findById(Bracket sourceBracket) {
		var bracket = bracketService.findById(sourceBracket.getId());

		assertThat(bracket).isNotEmpty().get().usingRecursiveComparison()
				.ignoringFields("localization", "id", "translations")
				.isEqualTo(sourceBracket);
	}

	@Test
	void save() {
		Mockito.clearInvocations(bracketRepository);
		var staff = BracketTestProvider.staffDTO();

		staff.setName("Staff_test");

		var captor = ArgumentCaptor.forClass(Bracket.class);
		var id = bracketService.saveBracket(staff);

		assertThat(id).isNotNull().isEqualTo(staff.getId());
		Mockito.verify(bracketRepository).save(captor.capture());
		assertThat(captor.getValue()).usingRecursiveComparison()
				.ignoringFields("cards", "pcaBracket", "translations")
				.isEqualTo(staff);
	}

	private static Stream<Arguments> provideValidBrackets() {
	    return BracketTestProvider.provideValidBrackets().stream().map(Arguments::of);
	}

}
