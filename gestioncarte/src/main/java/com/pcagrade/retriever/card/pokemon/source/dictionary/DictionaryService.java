package com.pcagrade.retriever.card.pokemon.source.dictionary;

import com.pcagrade.retriever.cache.IEvictable;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.translation.IPokemonCardTranslatorFactory;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.localization.translation.ITranslator;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DictionaryService implements IEvictable, IPokemonCardTranslatorFactory {

	private static final Logger LOGGER = LogManager.getLogger(DictionaryService.class);

	private final DictionaryTranslator translator = new DictionaryTranslator();

	@Autowired
	private DictionaryEntryRepository dictionaryEntryRepository;

	public String translate(Localization from, Localization to, String value) {
		return dictionaryEntryRepository.findFirstByFromAndToAndFromValueIgnoreCaseOrderByCountDesc(from, to, value)
				.map(DictionaryEntry::getToValue)
				.orElse("");
	}

	@Override
	public ITranslator<PokemonCardTranslationDTO> createTranslator(PokemonSetDTO set, PokemonCardDTO card) {
		return translator;
	}

	@Scheduled(cron = "${retriever.schedule.dictionary-refresh}")
	public void refreshDictionary() {
		LOGGER.info("Refreshing dictionary");
		dictionaryEntryRepository.refreshDictionary();
	}

	@Override
	public void evict() {
		refreshDictionary();
	}

	private class DictionaryTranslator implements ITranslator<PokemonCardTranslationDTO> {

		@Override
		public Optional<PokemonCardTranslationDTO> translate(PokemonCardTranslationDTO source, Localization localization) {
			if (source != null) {
				var value = DictionaryService.this.translate(source.getLocalization(), localization, source.getName());
				var labelValue = DictionaryService.this.translate(source.getLocalization(), localization, source.getLabelName());

				return StringUtils.isNotBlank(value) ? Optional.of(createTranslation(source, localization, value, labelValue)) : Optional.empty();
			}
			return Optional.empty();
		}

		private PokemonCardTranslationDTO createTranslation(PokemonCardTranslationDTO source, Localization to, String name, String labelName) {
			var target = source.copy();

			target.setLocalization(to);
			target.setName(name);
			target.setLabelName(StringUtils.isNotBlank(labelName) ? labelName : name);
			target.setTrainer("");
			return target;
		}

		@Override
		public String getName() {
			return "pca-translation-dictionary";
		}
	}
}
