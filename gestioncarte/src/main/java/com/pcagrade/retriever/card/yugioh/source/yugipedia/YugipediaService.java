package com.pcagrade.retriever.card.yugioh.source.yugipedia;

import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.yugioh.YuGiOhCardDTO;
import com.pcagrade.retriever.card.yugioh.YuGiOhCardHelper;
import com.pcagrade.retriever.card.yugioh.extraction.IYuGiOhCardSourceService;
import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetDTO;
import com.pcagrade.retriever.card.yugioh.set.extraction.IYuGiOhSetSourceService;
import com.pcagrade.retriever.card.yugioh.translation.YuGiOhCardTranslationDTO;
import com.pcagrade.retriever.extraction.consolidation.source.INamedConsolidationSource;
import com.pcagrade.retriever.extraction.consolidation.source.SimpleConsolidationSource;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.progress.IProgressTracker;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

@Service
@Order(0)
public class YugipediaService implements IYuGiOhCardSourceService, IYuGiOhSetSourceService {

    private static final List<Localization> SUPPORTED_LOCALIZATIONS = List.of(Localization.USA, Localization.JAPAN, Localization.FRANCE, Localization.GERMANY, Localization.ITALY, Localization.PORTUGAL);

    @Autowired
    private YugipediaParser yugipediaParser;
    @Autowired
    private YugipediaMapper yugipediaMapper;

    @Override
    public List<INamedConsolidationSource<YuGiOhCardDTO>> extractCards(YuGiOhSetDTO set, IProgressTracker tracker) {
        if (tracker != null) {
            tracker.restart("Extraction yugipedia.com", SUPPORTED_LOCALIZATIONS.size());
        }

        var groups = SUPPORTED_LOCALIZATIONS.stream()
                .<YugipediaCard>mapMulti((l, downstream) -> {
                    extractCards(set, l).forEach(downstream);
                    if (tracker != null) {
                        tracker.makeProgress();
                    }
                })
                .collect(PCAUtils.groupMatching(this::shouldMergeCards));

        if (tracker != null) {
            tracker.restart("Extraction yugipedia.com", groups.size());
        }
        return groups.stream()
                .map(list -> {
                    var cardMap = new EnumMap<Localization, YugipediaCard>(Localization.class);

                    list.forEach(c -> cardMap.put(c.localization(), c));
                    if (tracker != null) {
                        tracker.makeProgress();
                    }

                    var dto = yugipediaMapper.mapToDTO(cardMap);

                    dto.getSetIds().add(set.getId());
                    return createSource(dto);
                }).toList();
    }

    private List<YugipediaCard> extractCards(YuGiOhSetDTO set, Localization l) {
        var yugipediaSets = set.getYugipediaSets().stream()
                .filter(s -> s.localization() == l)
                .toList();

        if (CollectionUtils.isNotEmpty(yugipediaSets)) {
            return yugipediaSets.stream()
                    .<YugipediaCard>mapMulti((s, downstream) -> yugipediaParser.getCards(s.url(), l).forEach(downstream))
                    .toList();
        }
        return Collections.emptyList();
    }

    private boolean shouldMergeCards(YugipediaCard card1, YugipediaCard card2) {
        if (StringUtils.equalsIgnoreCase(card1.name(), card2.name())) {
            return true;
        }
        return StringUtils.equals(YuGiOhCardHelper.extractCardNumber(card1.number()), YuGiOhCardHelper.extractCardNumber(card2.number())) && StringUtils.equalsIgnoreCase(card1.rarity(), card2.rarity());
    }

    private INamedConsolidationSource<YuGiOhCardDTO> createSource(YuGiOhCardDTO card) {
        var translations = card.getTranslations();
        var number = SUPPORTED_LOCALIZATIONS.stream()
                .map(translations::get)
                .filter(Objects::nonNull)
                .findFirst()
                .map(YuGiOhCardTranslationDTO::getNumber)
                .orElse("");

        return createSource(card, StringUtils.isNotBlank(number) ? "https://yugipedia.com/index.php?title=" + number : "");
    }

    @Nonnull
    private static <T> INamedConsolidationSource<T> createSource(T dto, String link) {
        return new SimpleConsolidationSource<>(dto, 3, "yugipedia", link);
    }

    @Override
    public List<INamedConsolidationSource<YuGiOhSetDTO>> getAllSets() {
        var sourceSets = yugipediaParser.getAllSets();
        var mainSets = sourceSets.stream()
                .filter(s -> StringUtils.isBlank(s.mainSet()))
                .map(s -> Pair.of(s, new ArrayList<ParsedYugipediaSet>()))
                .toList();

        sourceSets.stream()
                .filter(s -> StringUtils.isNotBlank(s.mainSet()))
                .forEach(s -> mainSets.stream()
                            .filter(m -> StringUtils.equalsIgnoreCase(m.getLeft().name(), s.mainSet()))
                            .findFirst().ifPresent(p -> p.getRight().add(s)));
        return mainSets.stream()
                .map(e -> {
                    var set = yugipediaMapper.mapToDTO(e.getLeft());

                    e.getRight().forEach(s -> {
                        set.getOfficialSitePids().addAll(yugipediaMapper.mapOfficialSitePids(s));
                        set.getYugipediaSets().addAll(s.sets());
                    });
                    return createSource(set, e.getLeft().link());
                })
                .toList();
    }


}
