package com.pcagrade.retriever.card.yugioh.source.official;

import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.cache.CacheService;
import com.pcagrade.retriever.card.yugioh.YuGiOhCardDTO;
import com.pcagrade.retriever.card.yugioh.YuGiOhCardHelper;
import com.pcagrade.retriever.card.yugioh.extraction.IYuGiOhCardSourceService;
import com.pcagrade.retriever.card.yugioh.serie.YuGiOhSerieDTO;
import com.pcagrade.retriever.card.yugioh.serie.YuGiOhSerieService;
import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetDTO;
import com.pcagrade.retriever.card.yugioh.set.extraction.IYuGiOhSetSourceService;
import com.pcagrade.retriever.extraction.consolidation.source.IConsolidationSource;
import com.pcagrade.retriever.extraction.consolidation.source.INamedConsolidationSource;
import com.pcagrade.retriever.extraction.consolidation.source.SimpleConsolidationSource;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.progress.IProgressTracker;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class YuGiOhOfficialSiteService implements IYuGiOhCardSourceService, IYuGiOhSetSourceService {
    private static final Pattern ORS_PATTERN = Pattern.compile("(?:^|\\s)OTS(?:$|\\s)", Pattern.CASE_INSENSITIVE);
    private static final Pattern INTEGER_PATTERN = Pattern.compile("-?\\d+");
    public static final String NAME = "official-site";

    @Autowired
    private YuGiOhOfficialSiteParser yuGiOhOfficialSiteParser;
    @Autowired
    private YuGiOhOfficialSiteMapper yuGiOhOfficialSiteMapper;
    @Autowired
    private YuGiOhSerieService yuGiOhSeriesService;
    @Autowired
    private CacheService cacheService;

    @Override
    public List<INamedConsolidationSource<YuGiOhSetDTO>> getAllSets() {
        return cacheService.getCached("allYuGiOhOfficialSets", () -> getGroupedSets().stream()
                .filter(CollectionUtils::isNotEmpty)
                .map(l -> (INamedConsolidationSource<YuGiOhSetDTO>) new OfficialSiteSetConsolidationSource(this.maToSetDTO(l)))
                .toList());
    }

    private YuGiOhSetDTO maToSetDTO(List<OfficialSiteSet> list) {
        var set = yuGiOhOfficialSiteMapper.mapToSetDTO(list);
        var first = list.get(0);

        yuGiOhSeriesService.findSerieByNameAndLocalisation(first.serie(), first.localization()).ifPresent(s -> set.setSerieId(s.getId()));
        return set;
    }

    private List<List<OfficialSiteSet>> getGroupedSets() {
        return cacheService.getCached("groupedYuGiOhOfficialSets", () -> yuGiOhOfficialSiteParser.getAllSets().stream()
                .collect(PCAUtils.groupMatching(this::shouldGroupSets)));
    }

    // TODO visible for testing
    boolean shouldGroupSets(OfficialSiteSet set1, OfficialSiteSet set2) {
        var pid1 = set1.pid();
        var pid2 = set2.pid();
        var localization1 = set1.localization();
        var localization2 = set2.localization();
        var name1 = set1.name();
        var name2 = set2.name();
        var serie1 = set1.serie();
        var serie2 = set2.serie();

        if (isDifferentLocalizationGroup(localization1, localization2)) {
            return false;
        } else if (StringUtils.equalsIgnoreCase(pid1, pid2) || (StringUtils.equalsIgnoreCase(name1, name2)) && (StringUtils.equalsIgnoreCase(serie1, serie2) || StringUtils.isAnyBlank(serie1, serie2))) {
            return true;
        } else if (localization1 == Localization.JAPAN && localization2 == Localization.JAPAN && (StringUtils.containsIgnoreCase(name1, name2) || StringUtils.containsIgnoreCase(name2, name1))) {
            return true;
        }

        if (ORS_PATTERN.matcher(name1).find() && ORS_PATTERN.matcher(name2).find()) {
            var n1 = getOtsNumber(name1);
            var n2 = getOtsNumber(name2);

            return n1 != -1 && n1 == n2;
        }
        return false;
    }

    private int getOtsNumber(String name) {
        var match = INTEGER_PATTERN.matcher(name);

        return match.find() ? Integer.parseInt(match.group()) : -1;
    }

    private static boolean isDifferentLocalizationGroup(Localization localization1, Localization localization2) {
        return localization1 != localization2 && Localization.Group.WEST.contains(localization1) != Localization.Group.WEST.contains(localization2);
    }

    public Optional<YuGiOhSerieDTO> getSerieWithPid(String pid, Localization localization) {
        if (StringUtils.isBlank(pid) || localization == null) {
            return Optional.empty();
        }

        return getGroupedSets().stream()
                .filter(l -> {
                    if (CollectionUtils.isEmpty(l)) {
                        return false;
                    }

                    return l.stream().anyMatch(s -> StringUtils.equalsIgnoreCase(s.pid(), pid) && s.localization() == localization);
                }).findFirst()
                .map(yuGiOhOfficialSiteMapper::mapToSerieDTO);
    }

    public String getOfficialSiteSetNameWithPid(String pid, Localization localization) {
        if (StringUtils.isBlank(pid) || localization == null) {
            return "";
        }

        return yuGiOhOfficialSiteParser.getAllSets().stream()
                .filter(s -> StringUtils.equalsIgnoreCase(s.pid(), pid) && s.localization() == localization)
                .map(OfficialSiteSet::name)
                .findFirst()
                .orElse("");
    }

    @Override
    public List<INamedConsolidationSource<YuGiOhCardDTO>> extractCards(YuGiOhSetDTO set, @Nullable IProgressTracker tracker) {
        if (tracker != null) {
            tracker.restart("Extraction site officiel Yu-Gi-Oh!", set.getOfficialSitePids().size());
        }
        return set.getOfficialSitePids().stream()
                .<OfficialSiteCard>mapMulti((pid, downstream) -> {
                    if (tracker != null) {
                        tracker.makeProgress();
                    }
                    yuGiOhOfficialSiteParser.getCards(pid.pid(), pid.localization()).forEach(downstream);
                })
                .collect(PCAUtils.groupMatching(this::shouldMergeCards)).stream()
                .map(c -> {
                    var dto = yuGiOhOfficialSiteMapper.mapToDTO(c);

                    dto.getSetIds().add(set.getId());
                    return createSource(dto, yuGiOhOfficialSiteParser.getCardUrl(c.get(0).cid(), c.get(0).localization()).toString());
                })
                .toList();
    }

    private <T> INamedConsolidationSource<T> createSource(T dto, String link) {
        return new SimpleConsolidationSource<>(dto, 1, NAME, link);
    }

    private boolean shouldMergeCards(OfficialSiteCard card1, OfficialSiteCard card2) {
        if (card1.sneakPeek() != card2.sneakPeek() || !StringUtils.equalsIgnoreCase(card1.rarity().code(), card2.rarity().code())) {
            return false;
        } else if (StringUtils.equalsIgnoreCase(card1.name(), card2.name())) {
            return true;
        }
        return StringUtils.equals(YuGiOhCardHelper.extractCardNumber(card1.number()), YuGiOhCardHelper.extractCardNumber(card2.number()));
    }

    private record OfficialSiteSetConsolidationSource(
            YuGiOhSetDTO set
    ) implements INamedConsolidationSource<YuGiOhSetDTO> {

        @Nonnull
        @Override
        public <U> IConsolidationSource<U> with(U newValue) {
            return new SimpleConsolidationSource<>(newValue, 1, NAME, "");
        }

        @Nonnull
        @Override
        public YuGiOhSetDTO getValue() {
            return set;
        }

        @Override
        public int getWeight() {
            return 1;
        }

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public boolean isSameSource(IConsolidationSource<?> other) {
            return false;
        }
    }
}
