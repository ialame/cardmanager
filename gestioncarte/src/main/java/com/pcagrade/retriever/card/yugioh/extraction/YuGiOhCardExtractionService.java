package com.pcagrade.retriever.card.yugioh.extraction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import com.pcagrade.retriever.card.yugioh.YuGiOhCardDTO;
import com.pcagrade.retriever.card.yugioh.YuGiOhCardService;
import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetDTO;
import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetService;
import com.pcagrade.retriever.extraction.AbstractExtractionService;
import com.pcagrade.retriever.extraction.consolidation.ConsolidationService;
import com.pcagrade.retriever.extraction.consolidation.source.IConsolidationSource;
import com.pcagrade.retriever.extraction.consolidation.source.INamedConsolidationSource;
import com.pcagrade.retriever.extraction.consolidation.source.SimpleConsolidationSource;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.progress.ConsolidatedProgressTracker;
import com.pcagrade.retriever.progress.IProgressTracker;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import jakarta.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class YuGiOhCardExtractionService extends AbstractExtractionService<ExtractedYuGiOhCardDTO> {

    private static final Logger LOGGER = LogManager.getLogger(YuGiOhCardExtractionService.class);

    public static final String NAME = "yugioh_cards";

    public static final String TRACKER_MESSAGE = "Extraction des cartes Yu-Gi-Oh!";

    @Autowired
    private YuGiOhSetService yuGiOhSetService;
    @Autowired
    private ConsolidationService consolidationService;
    @Autowired
    private YuGiOhCardService yugiohCardService;
    @Autowired(required = false)
    private List<IYuGiOhCardSourceService> sourceServices;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ConsolidatedProgressTracker progressTracker = new ConsolidatedProgressTracker(TRACKER_MESSAGE);

    protected YuGiOhCardExtractionService(ObjectMapper mapper) {
        super(NAME, mapper, ExtractedYuGiOhCardDTO.class);
    }

    public List<ExtractedYuGiOhCardDTO> extractYuGiOhCards(Ulid setId) {
        return extractYuGiOhCards(setId, null);
    }

    private List<ExtractedYuGiOhCardDTO> extractYuGiOhCards(Ulid setId, @Nullable ConsolidatedProgressTracker tracker) {
        return yuGiOhSetService.findById(setId)
                .map(set -> extractCards(set, tracker))
                .orElse(Collections.emptyList());
    }

    public void startExtraction(Ulid setId) {
        executorService.execute(() -> {
            progressTracker.restart(TRACKER_MESSAGE, 0);
            this.putCards(this.extractYuGiOhCards(setId, progressTracker));
            progressTracker.restart(TRACKER_MESSAGE, 0);
        });
    }

    private boolean shouldGroupCards(YuGiOhCardDTO card1, YuGiOhCardDTO card2) {
        var localizations = Localization.Group.YUGIOH.getLocalizations();

        for (var localization : localizations) {
            var t1 = card1.getTranslations().get(localization);
            var t2 = card2.getTranslations().get(localization);

            if (t1 != null && t2 != null && StringUtils.equalsIgnoreCase(t1.getNumber(), t2.getNumber()) && rarityMatch(t1.getRarity(), t2.getRarity())) {
                return true;
            }
        }
        return false;
    }

    private boolean rarityMatch(String rarity1, String rarity2) {
        return StringUtils.equalsIgnoreCase(rarity1, rarity2) || (StringUtils.equalsAnyIgnoreCase(rarity1, "C", "SP") && StringUtils.equalsAnyIgnoreCase(rarity2, "C", "SP"));
    }

    private List<ExtractedYuGiOhCardDTO> extractCards(@Nonnull YuGiOhSetDTO set, @Nullable ConsolidatedProgressTracker tracker) {
        if (CollectionUtils.isEmpty(sourceServices)) {
            return Collections.emptyList();
        }

        return createConsolidationGroups(sourceServices.parallelStream()
                .map(source -> safeExtractCards(set, tracker, source))
                .toList(), this::shouldGroupCards).stream()
                .map(this::createExtractedCard)
                .filter(Objects::nonNull)
                .filter(this::hasChanges)
                .toList();
    }

    private static List<INamedConsolidationSource<YuGiOhCardDTO>> safeExtractCards(@Nonnull YuGiOhSetDTO set, ConsolidatedProgressTracker tracker, IYuGiOhCardSourceService source) {
        try {
            var cards = source.extractCards(set, tracker != null ? tracker.createSubTracker() : null);

            LOGGER.info("Extracted {} cards from {}", cards::size, () -> source);
            return cards;
        } catch (Exception e) {
            LOGGER.error(() -> "Error while extracting cards from source " + source, e);
            return Collections.emptyList();
        }
    }


    @Nullable
    private ExtractedYuGiOhCardDTO createExtractedCard(List<? extends IConsolidationSource<YuGiOhCardDTO>> sources) {
        var card = consolidationService.consolidate(YuGiOhCardDTO.class, sources);

        if (card == null) {
            return null;
        }

        var extractedCard = new ExtractedYuGiOhCardDTO();
        var savedCards = yugiohCardService.findSavedCards(card);

        extractedCard.setId(UlidCreator.getUlid());
        extractedCard.setCard(card);
        extractedCard.setSavedCards(savedCards);
        extractedCard.setSources(sources.stream()
                .map(SimpleConsolidationSource::from)
                .toList());
        return extractedCard;
    }

    private boolean hasChanges(ExtractedYuGiOhCardDTO extractedCard) {
        var card = extractedCard.getCard();

        return extractedCard.getSavedCards().stream().allMatch(savedCard -> YuGiOhCardDTO.CHANGES_COMPARATOR.compare(card, savedCard) != 0);
    }

    public void putCards(List<ExtractedYuGiOhCardDTO> list) {
        this.put(list, (e1, e2) -> YuGiOhCardDTO.CHANGES_COMPARATOR.compare(e1.getCard(), e2.getCard()));
    }

    public Optional<IProgressTracker> getProgress() {
        return progressTracker.getMax() > 0 ? Optional.of(progressTracker) : Optional.empty();
    }
}
