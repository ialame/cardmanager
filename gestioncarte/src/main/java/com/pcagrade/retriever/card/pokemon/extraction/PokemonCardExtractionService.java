package com.pcagrade.retriever.card.pokemon.extraction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import com.pcagrade.mason.commons.MapMultiHelper;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.card.pokemon.IPokemonCardService;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.PokemonCardHelper;
import com.pcagrade.retriever.card.pokemon.extraction.history.PokemonCardExtractionHistoryService;
import com.pcagrade.retriever.card.pokemon.feature.FeatureService;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetService;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.ExtractedBulbapediaCard;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction.BulbapediaExtractionService;
import com.pcagrade.retriever.card.pokemon.source.official.jp.JapaneseOfficialSiteService;
import com.pcagrade.retriever.card.pokemon.source.pkmncards.PkmncardsComService;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationDTO;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationService;
import com.pcagrade.retriever.extraction.AbstractExtractionService;
import com.pcagrade.retriever.progress.ConsolidatedProgressTracker;
import com.pcagrade.retriever.progress.IProgressTracker;
import jakarta.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
public class PokemonCardExtractionService extends AbstractExtractionService<ExtractedPokemonCardDTO> {

    public static final String NAME = "pokemon_cards";

    private static final Logger LOGGER = LogManager.getLogger(PokemonCardExtractionService.class);
    public static final String TRACKER_MESSAGE = "Extraction des cartes pokemon";

    @Autowired
    private BulbapediaExtractionService bulbapediaExtractionService;

    @Autowired
    private PokemonCardTranslationService pokemonCardTranslationService;

    @Autowired
    private PokemonSetService pokemonSetService;

    @Autowired
    private IPokemonCardService pokemonCardService;

    @Autowired
    private FeatureService featureService;

    @Autowired
    private PkmncardsComService pkmncardsComService;

    @Autowired
    private JapaneseOfficialSiteService japaneseOfficialSiteService;

    @Autowired
    private PokemonCardExtractionHistoryService pokemonCardExtractionHistoryService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ConsolidatedProgressTracker progressTracker = new ConsolidatedProgressTracker(TRACKER_MESSAGE);

    protected PokemonCardExtractionService(ObjectMapper mapper) {
        super(NAME, mapper, ExtractedPokemonCardDTO.class);
    }


    public int countPokemonCardsToExtract(Ulid setId, boolean distribution, List<String> filters) {
        return bulbapediaExtractionService.getCardCountFromBulbapedia(setId, distribution, filters);
    }

    public List<ExtractedPokemonCardDTO> extractPokemonCards(Ulid setId, boolean distribution, List<String> filters) {
        return extractPokemonCards(setId, distribution, filters, null);
    }

    private List<ExtractedPokemonCardDTO> extractPokemonCards(Ulid setId, boolean distribution, List<String> filters, @Nullable ConsolidatedProgressTracker tracker) {
        LOGGER.info("Extracting pokemon cards for set {}", setId);

        return postProcess(setId, distribution, bulbapediaExtractionService.getCardsBySetId(setId, distribution, filters, createSubTracker(tracker)), createSubTracker(tracker));
    }

    @Nullable
    private static IProgressTracker createSubTracker(ConsolidatedProgressTracker tracker) {
        return tracker == null ? null : tracker.createSubTracker();
    }

    @Nonnull
    private List<ExtractedPokemonCardDTO> postProcess(Ulid setId, boolean distribution, List<ExtractedBulbapediaCard> cards, @Nullable IProgressTracker tracker) {
        var size = cards.size();

        if (tracker != null) {
            tracker.restart("Traduction et post-processing", size);
        }
        var extractedCards = cards.parallelStream().<ExtractedPokemonCardDTO>mapMulti((bulbapediaCard, downstream) -> {
            try {
                var card = bulbapediaCard.card();
                var extractedCard = new ExtractedPokemonCardDTO();

                extractedCard.setId(UlidCreator.getUlid());
                extractedCard.setCard(card);
                extractedCard.setRawExtractedCard(card);
                handlePCAFeatures(card);
                extractedCard.setBulbapediaStatus(bulbapediaCard.status());
                card.getSetIds().forEach(i -> pokemonSetService.findSet(i).ifPresent(set -> translate(extractedCard, set)));

                LOGGER.info("Translated card: {}", card::getDisplay);

                if (distribution || card.isAlternate()) {
                    var parentCards = pokemonCardService.findParentCard(card);

                    if (CollectionUtils.isNotEmpty(parentCards)) {
                        var parent = parentCards.isEmpty() ? null :parentCards.get(0);

                        extractedCard.setParentCards(parentCards);
                        card.setParentId(parent.getId());
                        this.inherit(card, parent);
                    }
                }
                handleUnnumbered(card);
                if (hasChanges(extractedCard)) { // This needs to be the last step or else the card could be considered different from previous extractions
                    downstream.accept(extractedCard);
                }
            } catch (Exception e) {
                LOGGER.error("Error while extracting card", e);
            } finally {
                if (tracker != null) {
                    tracker.makeProgress();
                }
            }
        }).toList();

        LOGGER.info("Pokemon card extraction complete for set {}. {} cards extracted, {} retained.", setId, size, extractedCards.size());
        return extractedCards;
    }

    private boolean hasChanges(ExtractedPokemonCardDTO extractedCard) {
        var card = extractedCard.getCard();
        var savedCards = pokemonCardService.findSavedCards(card);
        var history = card.getSetIds().stream()
                .<PokemonCardDTO>mapMulti((i, d) -> pokemonCardExtractionHistoryService.getExtractionHistory(i).forEach(d))
                .toList();

        extractedCard.setSavedCards(savedCards);
        return Stream.concat(savedCards.stream(), history.stream()).allMatch(savedCard -> PokemonCardDTO.CHANGES_COMPARATOR.compare(card, savedCard) != 0);
    }

    private void handleUnnumbered(PokemonCardDTO card) {
        var jp = card.getTranslations().get(Localization.JAPAN);

        if (jp == null) {
            return;
        }

        var setIds = card.getSetIds();
        var number = jp.getNumber();

        List.copyOf(setIds).stream() // copy list to prevent a concurrency exception
                .mapMulti(MapMultiHelper.mapOptional(pokemonSetService::findSet))
                .filter(s -> {
                    if (!s.getTranslations().containsKey(Localization.JAPAN)) {
                        return false;
                    }

                    var totalNumber = s.getTotalNumber();

                    return StringUtils.isNotBlank(totalNumber) && !PokemonCardHelper.isNoNumber(totalNumber) && PokemonCardHelper.inUnnumbered(number, totalNumber);
                }).forEach(s -> setIds.remove(s.getId()));
    }

    private void translate(ExtractedPokemonCardDTO extractedCard, PokemonSetDTO set) {
        var card = extractedCard.getCard();

        translate(set, Localization.USA, card, extractedCard);
        translate(set, Localization.JAPAN, card, extractedCard);
        if (set.getTranslations().containsKey(Localization.USA)) {
            var artist = pkmncardsComService.getCardArtist(set, card);

            if (StringUtils.isNotBlank(artist)) {
                card.setArtist(artist);
            }
        }
        if (set.getTranslations().containsKey(Localization.JAPAN)) {
            var artist = japaneseOfficialSiteService.getCardArtist(set, card);

            if (StringUtils.isNotBlank(artist)) {
                card.setArtist(artist);
            }
        }
    }

    private void translate(PokemonSetDTO set, Localization localization, PokemonCardDTO card, ExtractedPokemonCardDTO extractedCard) {
        if (set.getTranslations().containsKey(localization)) {
            var translationSources = pokemonCardTranslationService.getCardTranslations(set, card, localization);

            extractedCard.getTranslations().addAll(translationSources);

            var translations = card.getTranslations();

            translations.putAll(pokemonCardTranslationService.translateCard(card, translationSources));
            if (!translations.containsKey(localization)) {
                LOGGER.warn("No translation found for card {} for localization {}", card::getDisplay, localization::name);
            } else {
                LOGGER.debug("Translated: {} ({})",
                        () -> translations.get(localization).getName(),
                        () -> translations.get(localization).getNumber());
            }
        }
    }

    public void startExtraction(Ulid setId, boolean distribution, List<String> filters) {
        executorService.execute(() -> {
            progressTracker.restart(TRACKER_MESSAGE, 0);
            this.putCards(this.extractPokemonCards(setId, distribution, filters, progressTracker));
            progressTracker.restart(TRACKER_MESSAGE, 0);
        });
    }

    public void putCards(List<ExtractedPokemonCardDTO> list) {
        this.put(list, ExtractedPokemonCardDTO.CHANGES_COMPARATOR);
    }

    private void inherit(PokemonCardDTO card, PokemonCardDTO parent) {
        var translations = card.getTranslations();

        parent.getTranslations().forEach((l, t) -> {
            if (Localization.Group.WEST.contains(l)) {
                var old = translations.getOrDefault(l, t);
                var translation = new PokemonCardTranslationDTO();

                translation.setLocalization(l);

                inheritIfNotNull(t::getName, translation::setName);
                inheritIfNotNull(t::getLabelName, translation::setLabelName);
                inheritIfNotNull(t::getOriginalName, translation::setOriginalName);

                inheritIfNotNull(old::getNumber, translation::setNumber);
                inheritIfNotNull(old::getRarity, translation::setRarity);

                translation.setAvailable(old.isAvailable());
                translations.put(l, translation);
            }
        });
        card.setFullArt(parent.isFullArt());
    }

    private void inheritIfNotNull(Supplier<String> supplier, Consumer<String> consumer) {
        var value = supplier.get();

        if (StringUtils.isNotBlank(value)) {
            consumer.accept(value);
        }
    }

    private void handlePCAFeatures(PokemonCardDTO card) {
        var savedCards = pokemonCardService.findSavedCards(card);

        if (CollectionUtils.isNotEmpty(savedCards)) {
            for (var feature : featureService.getPCAFeatures()) {
                var id = feature.getId();

                if (savedCards.stream().allMatch(c -> c.getFeatureIds().contains(id))) {
                    card.getFeatureIds().add(id);
                    card.getTranslations().values().forEach(t -> featureService.applyPCAFeatureToTranslation(feature, t));
                }
            }
        }
    }

    public Optional<IProgressTracker> getProgress() {
        return progressTracker.getMax() > 0 ? Optional.of(progressTracker) : Optional.empty();
    }
}
