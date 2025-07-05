package com.pcagrade.retriever.card.pokemon.translation;

import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.feature.FeatureService;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetService;
import com.pcagrade.retriever.card.pokemon.trainer.KnownTrainerService;
import com.pcagrade.retriever.card.pokemon.translation.translator.PokemonCardTranslatorService;
import com.pcagrade.retriever.extraction.consolidation.ConsolidationService;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.localization.translation.ITranslator;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PokemonCardTranslationService {

	private static final Logger LOGGER = LogManager.getLogger(PokemonCardTranslationService.class);

	private static final Map<Localization, Localization.Group>TRANSLATABLE_LOCALIZATIONS = Map.of(
			Localization.USA, Localization.Group.WEST,
			Localization.JAPAN, Localization.Group.JAPAN);

	@Autowired
	private PokemonCardTranslatorService pokemonCardTranslatorService;

	@Autowired
	private PokemonSetService pokemonSetService;

	@Autowired
	private FeatureService featureService;

	@Autowired
	private KnownTrainerService knownTrainerService;

	@Autowired
	private ConsolidationService consolidationService;

	@Autowired(required = false)
	private List<IPokemonCardTranslatorFactory> translatorFactories;

	public List<SourcedPokemonCardTranslationDTO> getCardTranslations(PokemonSetDTO set, PokemonCardDTO card, Localization from) {
		var sourceTranslation = card.getTranslations().get(from);

		if (sourceTranslation == null || !TRANSLATABLE_LOCALIZATIONS.containsKey(from)) {
			return Collections.emptyList();
		}

		var validTranslations = TRANSLATABLE_LOCALIZATIONS.get(from).stream()
				.filter(localization -> isLocalizationAvailable(set, localization))
				.toList();
		var translators = getTranslators(card, set, from);

		LOGGER.info("Translating card {} to: {}", card::getDisplay, () -> validTranslations.stream().map(Localization::getCode).collect(Collectors.joining(", ")));

		return validTranslations.stream()
				.<SourcedPokemonCardTranslationDTO>mapMulti((to, downstream) -> getAllTranslations(sourceTranslation, translators, to).forEach((k, translation) -> {
					if (StringUtils.isBlank(translation.getTrainer())) {
						var knownTrainer = knownTrainerService.extractTrainer(Localization.FRANCE, translation.getName()).orElse(null);

						if (knownTrainer != null) {
							translation.setTrainer(knownTrainer.getTrainer());
							translation.setName(knownTrainer.getName());
							translation.setLabelName(knownTrainer.getLabelName());
						}
					}
					featureService.applyFeaturePatterns(card, translation, k.getName());
					featureService.applyPCAFeatures(card, translation);
					downstream.accept(createSourcedPokemonCardTranslation(to, k, translation));
				}))
				.toList();
	}

	private SourcedPokemonCardTranslationDTO createSourcedPokemonCardTranslation(Localization localization, ITranslator<PokemonCardTranslationDTO> translator, PokemonCardTranslationDTO translation) {
		return new SourcedPokemonCardTranslationDTO(localization, translator.getName(), this.getWeight(translator), translator instanceof ITranslationSourceUrlProvider provider ? provider.getUrl(localization) : "", translation);
	}

	public Map<Localization, PokemonCardTranslationDTO> translateCard(PokemonCardDTO card, List<SourcedPokemonCardTranslationDTO> translations) {
		var map = card.getTranslations();

		translations.stream().collect(Collectors.groupingBy(SourcedPokemonCardTranslationDTO::localization)).forEach((localization, translationList) -> {
			if (!CollectionUtils.isEmpty(translationList)) {
				map.put(localization, consolidationService.consolidate(PokemonCardTranslationDTO.class, translationList));
			}
		});
		return map;
	}

	private int getWeight(ITranslator<PokemonCardTranslationDTO> translator) {
		return pokemonCardTranslatorService.getWeight(translator.getName());
	}

	private List<ITranslator<PokemonCardTranslationDTO>> getTranslators(PokemonCardDTO card, PokemonSetDTO set, Localization from) {
		if (CollectionUtils.isEmpty(translatorFactories)) {
			return Collections.emptyList();
		}
		var list = translatorFactories.stream()
				.map(f -> f.createTranslator(set, card))
				.collect(Collectors.toList());

		if (from == Localization.JAPAN) {
			var usTranslation = card.getTranslations().get(Localization.USA);

			if (usTranslation != null) {
				getSet(card, Localization.USA).forEach(s -> getTranslators(card, s, Localization.USA).stream()
						.map(t -> new UsToJapanTranslatorWrapper(t, usTranslation))
						.forEach(list::add));
			}
		}
		return List.copyOf(list);
	}

	private boolean isLocalizationAvailable(PokemonSetDTO set, Localization localization) {
		var translation = set.getTranslations().get(localization);

		return translation != null && translation.isAvailable();
	}

	public List<SourcedPokemonCardTranslationDTO> getTranslations(PokemonCardDTO card, Localization from) {
		var translation = card.getTranslations().get(from);

		if (translation == null) {
			return Collections.emptyList();
		}

		return getSet(card, from)
				.<SourcedPokemonCardTranslationDTO>mapMulti((set, downstream) -> getCardTranslations(set, card, from).forEach(downstream))
				.toList();
	}

	@Nonnull
	private Stream<PokemonSetDTO> getSet(PokemonCardDTO card, Localization from) {
		return card.getSetIds().stream()
				.map(pokemonSetService::findSet)
				.<PokemonSetDTO>mapMulti(Optional::ifPresent)
				.filter(s -> isLocalizationAvailable(s, from))
				.limit(1);
	}

	@Nonnull
	private Map<ITranslator<PokemonCardTranslationDTO>, PokemonCardTranslationDTO> getAllTranslations(PokemonCardTranslationDTO sourceTranslation, List<ITranslator<PokemonCardTranslationDTO>> translators, Localization to) {
		return translators.stream()
				.<Pair<ITranslator<PokemonCardTranslationDTO>, PokemonCardTranslationDTO>>mapMulti((t, downstream) -> t.translate(sourceTranslation, to).ifPresent(pokemonCardTranslationDTO -> downstream.accept(Pair.of(t, pokemonCardTranslationDTO))))
				.collect(PCAUtils.toMap());
	}

	public PokemonCardTranslationDTO getAllMatching(Collection<PokemonCardTranslationDTO> collection) {
		var list = collection.stream()
				.map(PokemonCardTranslationDTO::getName)
				.filter(StringUtils::isNotBlank)
				.toList();

	    if (!CollectionUtils.isEmpty(list) && Collections.frequency(list, list.get(0)) == list.size()) {
	    	return collection.stream()
	    			.filter(t -> StringUtils.isNotBlank(t.getName()))
	    			.findFirst()
	    			.orElse(null);
	    }
	    return null;
	}
}
