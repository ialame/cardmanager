package com.pcagrade.retriever.card.pokemon.bracket;

import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.bracket.translation.BracketTranslationMapper;
import com.pcagrade.retriever.card.pokemon.bracket.translation.BracketTranslationMapperImpl;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

import java.util.List;

@RetrieverTestConfiguration
public class BracketTestConfig {

	@Bean
	public BracketRepository bracketRepository() {
		var list = List.of(
				BracketTestProvider.normal(),
				BracketTestProvider.attack(),
				BracketTestProvider.speed(),
				BracketTestProvider.staff()
		);
		var bracketRepository = RetrieverTestUtils.mockRepository(BracketRepository.class, list, Bracket::getId);

		Mockito.when(bracketRepository.findFirstByNameIgnoreCaseOrderById(Mockito.anyString())).then(i -> {
			var name = i.getArgument(0, String.class);

			return list.stream()
					.filter(b -> b.getName().equalsIgnoreCase(name))
					.findFirst()
					.orElse(null);
		});
		return bracketRepository;
	}


	@Bean
	public BracketTranslationMapper bracketTranslationMapper() {
		return new BracketTranslationMapperImpl();
	}
	@Bean
	public BracketMapper bracketMapper() {
		return new BracketMapperImpl();
	}

	@Bean
	public BracketService bracketService() {
		return new BracketService();
	}
}
