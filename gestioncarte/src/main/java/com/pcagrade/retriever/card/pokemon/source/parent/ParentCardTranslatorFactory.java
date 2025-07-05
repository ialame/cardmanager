package com.pcagrade.retriever.card.pokemon.source.parent;

import com.pcagrade.retriever.card.pokemon.IPokemonCardService;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.translation.IPokemonCardTranslatorFactory;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.localization.translation.ITranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ParentCardTranslatorFactory implements IPokemonCardTranslatorFactory {

	@Autowired
	private IPokemonCardService pokemonCardService;

	@Override
	public ITranslator<PokemonCardTranslationDTO> createTranslator(PokemonSetDTO set, PokemonCardDTO card) {
		return new ParentCardTranslator(card);
	}

	private class ParentCardTranslator implements ITranslator<PokemonCardTranslationDTO> {

		private final PokemonCardDTO card;

		private ParentCardTranslator(PokemonCardDTO card) {
			this.card = card;
		}

		@Override
		public Optional<PokemonCardTranslationDTO> translate(PokemonCardTranslationDTO source, Localization localization) {
			var id = card.getParentId();

			if (id == null) {
				return Optional.empty();
			}

			return pokemonCardService.getCardById(id).flatMap(c -> {
				var translation = c.getTranslations().get(localization);

				if (translation != null) {
					var copy = translation.copy();

					copy.setNumber(source.getNumber());
					copy.setRarity(source.getRarity());
					return Optional.of(copy);
				}
				return Optional.empty();
			});
		}

		@Override
		public String getName() {
			return "parent-card";
		}

	}

}
