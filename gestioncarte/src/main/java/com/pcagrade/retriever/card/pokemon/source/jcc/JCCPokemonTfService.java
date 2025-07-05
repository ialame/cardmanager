package com.pcagrade.retriever.card.pokemon.source.jcc;

import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.PokemonCardHelper;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.translation.IPokemonCardTranslatorFactory;
import com.pcagrade.retriever.card.pokemon.translation.ITranslationSourceUrlProvider;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.localization.translation.ITranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JCCPokemonTfService implements IPokemonCardTranslatorFactory {

	@Autowired
	private IJCCPokemonTfParser jccPokemonTfParser;

	@Override
	public ITranslator<PokemonCardTranslationDTO> createTranslator(PokemonSetDTO set, PokemonCardDTO card) {
		return new JCCPokemonTfTranslator(set, card);
	}

	private class JCCPokemonTfTranslator implements ITranslator<PokemonCardTranslationDTO>, ITranslationSourceUrlProvider {

		private final PokemonSetDTO set;
		private final PokemonCardDTO card;

		private JCCPokemonTfTranslator(PokemonSetDTO set, PokemonCardDTO card) {
			this.set = set;
			this.card = card;
		}

		@Override
		public Optional<PokemonCardTranslationDTO> translate(PokemonCardTranslationDTO source, Localization localization) {
			if (localization == Localization.FRANCE && source != null) {
				return PokemonCardHelper.findTranslation(source.getNumber(), jccPokemonTfParser.parse(set)).map(p -> createTranslation(source, PokemonCardHelper.removeBoldBracket(card, p.getLeft(), localization), p.getRight()));
			}
			return Optional.empty();
		}

		@Override
		public String getUrl(Localization localization) {
			return jccPokemonTfParser.getUrl(set);
		}

		private PokemonCardTranslationDTO createTranslation(PokemonCardTranslationDTO source, String name, String labelName) {
			var target = source.copy();

			target.setLocalization(Localization.FRANCE);
			target.setName(name);
			target.setLabelName(labelName);
			target.setTrainer("");
			return target;
		}

		@Override
		public String getName() {
			return "jcc-pokemon-tf";
		}
	}

}
