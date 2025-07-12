package com.pcagrade.retriever.card.pokemon.source.official;

import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.PokemonCardHelper;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.translation.IPokemonCardTranslatorFactory;
import com.pcagrade.retriever.card.pokemon.translation.ITranslationSourceUrlProvider;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.localization.translation.ITranslator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OfficialSiteTranslationService implements IPokemonCardTranslatorFactory {

	@Autowired
	private OfficialSiteParser officialSiteParser;

	@Override
	public ITranslator<PokemonCardTranslationDTO> createTranslator(PokemonSetDTO set, PokemonCardDTO card) {
		return new OfficialSiteTranslator(set, card);
	}

	private class OfficialSiteTranslator implements ITranslator<PokemonCardTranslationDTO>, ITranslationSourceUrlProvider {

		private final PokemonSetDTO set;
		private final PokemonCardDTO card;
		private OfficialSiteTranslator(PokemonSetDTO set, PokemonCardDTO card) {
			this.set = set;
			this.card = card;
		}

		@Override
		public Optional<PokemonCardTranslationDTO> translate(PokemonCardTranslationDTO source, Localization localization) {
			var pair = PokemonCardHelper.extractTrainer(officialSiteParser.getCardName(set, card, localization));
			var name = PokemonCardHelper.removeBoldBracket(card, pair.getLeft(), localization);

			if (StringUtils.isBlank(name)) {
				return Optional.empty();
			}
			return Optional.of(createTranslation(source, name, pair.getRight(), localization));
		}

		private PokemonCardTranslationDTO createTranslation(PokemonCardTranslationDTO source, String cardName, String trainer, Localization localization) {
			var target = source.copy();

			target.setLocalization(localization);
			target.setName(cardName);
			target.setLabelName(cardName);
			target.setTrainer(trainer);
			return target;
		}

		@Override
		public String getUrl(Localization localization) {
			return officialSiteParser.getUrl(set, card, localization).stream()
					.findFirst()
					.orElse("");
		}

		@Override
		public String getName() {
			return "official-site";
		}
	}
}
