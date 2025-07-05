package com.pcagrade.retriever.card.yugioh.source.ygoprodeck;

import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.yugioh.YuGiOhCardDTO;
import com.pcagrade.retriever.card.yugioh.YuGiOhCardHelper;
import com.pcagrade.retriever.card.yugioh.extraction.IYuGiOhCardSourceService;
import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetDTO;
import com.pcagrade.retriever.card.yugioh.set.extraction.IYuGiOhSetSourceService;
import com.pcagrade.retriever.card.yugioh.set.translation.YuGiOhSetTranslationDTO;
import com.pcagrade.retriever.extraction.consolidation.source.INamedConsolidationSource;
import com.pcagrade.retriever.extraction.consolidation.source.SimpleConsolidationSource;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.progress.IProgressTracker;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.SimilarityScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class YgoProDeckService implements IYuGiOhCardSourceService, IYuGiOhSetSourceService {

    @Value("${retriever.ygoprodeck-com.match.threshold}")
    private double matchThreshold;

    @Autowired
    private SimilarityScore<Double> similarityScore;
    @Autowired
    private YgoProDeckParser ygoProDeckParser;
    @Autowired
    private YgoProDeckMapper ygoProDeckMapper;

    private static final List<Localization> SUPPORTED_LOCALIZATIONS = List.of(Localization.USA, Localization.FRANCE, Localization.GERMANY, Localization.ITALY, Localization.PORTUGAL);

    @Override
    public List<INamedConsolidationSource<YuGiOhSetDTO>> getAllSets() {
        return ygoProDeckParser.getSets().stream()
                .collect(Collectors.groupingBy(YgoProDeckSet::setCode)).values().stream()
                .map(sets -> {
                    var set = sets.get(0);
                    var dto = new YuGiOhSetDTO();
                    var us = new YuGiOhSetTranslationDTO();

                    us.setLocalization(Localization.USA);
                    us.setCode(set.setCode());
                    us.setName(set.setName());
                    us.setReleaseDate(set.releaseDate());
                    dto.getTranslations().put(Localization.USA, us);
                    return createSource(dto);
                }).toList();
    }

    @Override
    public List<INamedConsolidationSource<YuGiOhCardDTO>> extractCards(YuGiOhSetDTO set, @Nullable IProgressTracker tracker) {
        if (tracker != null) {
            tracker.restart("Extraction ygoprodeck.com", SUPPORTED_LOCALIZATIONS.size());
        }

        var groups = SUPPORTED_LOCALIZATIONS.stream()
                .<UnmappedCard>mapMulti((l, downstream) -> {
                    extractCards(set, l).forEach(downstream);
                    if (tracker != null) {
                        tracker.makeProgress();
                    }
                })
                .collect(PCAUtils.groupMatching(this::shouldMergeCards));

        if (tracker != null) {
            tracker.restart("Extraction ygoprodeck.com", groups.size());
        }
        return groups.stream()
                .map(list -> {
                    var cardMap = new EnumMap<Localization, YgoProDeckCard>(Localization.class);
                    var setMap = new EnumMap<Localization, YgoProDeckSet>(Localization.class);

                    list.forEach(c -> {
                        cardMap.put(c.localization(), c.card());
                        setMap.put(c.localization(), c.set());
                    });
                    if (tracker != null) {
                        tracker.makeProgress();
                    }

                    var dto = ygoProDeckMapper.mapToDTO(cardMap, setMap);

                    dto.getSetIds().add(set.getId());
                    return createSource(dto);
                }).toList();
    }

    private boolean shouldMergeCards(UnmappedCard card1, UnmappedCard card2) {
        if (!StringUtils.equalsIgnoreCase(card1.set().setRarityCode(), card2.set().setRarityCode())) {
            return false;
        } else if (StringUtils.equalsIgnoreCase(card1.card().name(), card2.card().name())) {
            return true;
        }
        return StringUtils.equals(YuGiOhCardHelper.extractCardNumber(card1.set().setCode()), YuGiOhCardHelper.extractCardNumber(card2.set().setCode()));
    }

    private List<UnmappedCard> extractCards(YuGiOhSetDTO set, Localization localization) {
        var translation = set.getTranslations().get(localization);

        if (translation == null) {
            return Collections.emptyList();
        }

        var code = translation.getCode();

        return ygoProDeckParser.getCards(translation.getCode(), localization).stream()
                .<UnmappedCard>mapMulti((c, downstream) -> c.cardSets().stream()
                        .filter(s -> StringUtils.startsWith(s.setCode(), code) && similarityScore.apply(translation.getName(), s.setName()) >= matchThreshold)
                        .forEach(s -> downstream.accept(new UnmappedCard(c, s, localization))))
                .toList();
    }

    private <T> INamedConsolidationSource<T> createSource(T card) {
        return new SimpleConsolidationSource<>(card, 1, "ygo-pro-deck", "");
    }

    private record UnmappedCard(
            YgoProDeckCard card,
            YgoProDeckSet set,
            Localization localization
    ) { }
}
