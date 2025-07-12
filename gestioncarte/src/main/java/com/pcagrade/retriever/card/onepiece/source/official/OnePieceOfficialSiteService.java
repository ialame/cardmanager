package com.pcagrade.retriever.card.onepiece.source.official;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.onepiece.OnePieceCardDTO;
import com.pcagrade.retriever.card.onepiece.serie.OnePieceSerieDTO;
import com.pcagrade.retriever.card.onepiece.serie.OnePieceSerieService;
import com.pcagrade.retriever.card.onepiece.serie.translation.OnePieceSerieTranslationDTO;
import com.pcagrade.retriever.card.onepiece.set.OnePieceSetDTO;
import com.pcagrade.retriever.card.onepiece.set.OnePieceSetService;
import com.pcagrade.mason.localization.Localization;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import jakarta.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@CacheConfig(cacheNames = "onePieceOfficialSiteService")
public class OnePieceOfficialSiteService {

    private static final Comparator<OnePieceOfficialSiteCard> DISTRIBUTION_COMPARATOR = Comparator.comparing(OnePieceOfficialSiteCard::distributions, PCAUtils.collectionComparator(StringUtils::compareIgnoreCase));

    @Autowired
    private OnePieceOfficialSiteParser onePieceOfficialSiteParser;
    @Autowired
    private OnePieceOfficialSiteMapper onePieceOfficialSiteMapper;
    @Autowired
    private OnePieceSerieService onePieceSerieService;
    @Autowired
    private OnePieceSetService onePieceSetService;

    public List<OnePieceSerieDTO> getSeries() {
        return getSetListToSerie()
                .map(OnePieceOfficialSiteService::createSerie)
                .toList();
    }

    public Optional<OnePieceSerieDTO> findSerieByCode(String code) {
        return getSetListToSerie()
                .filter(l -> l.stream().anyMatch(s -> StringUtils.equalsIgnoreCase(s.code(), code)))
                .findFirst()
                .map(OnePieceOfficialSiteService::createSerie);
    }

    @Nonnull
    private Stream<List<OnePieceOfficialSiteSet>> getSetListToSerie() {
        return getGroupedSets().values().stream()
                .collect(PCAUtils.groupMatching((l1, l2) -> l1.stream().anyMatch(s1 -> l2.stream().anyMatch(s2 -> StringUtils.equalsIgnoreCase(s1.serie(), s2.serie()))))).stream()
                .map(lists -> lists.stream()
                        .flatMap(Collection::stream)
                        .filter(s -> StringUtils.isNotBlank(s.serie()) && s.localization() != null)
                        .toList())
                .filter(CollectionUtils::isNotEmpty);
    }

    @Nonnull
    private static OnePieceSerieDTO createSerie(List<OnePieceOfficialSiteSet> list) {
        var serie = new OnePieceSerieDTO();
        var translations = serie.getTranslations();

        list.forEach(s -> {
            var localization = s.localization();
            var serieName = s.serie();

            if (StringUtils.isBlank(serieName) || localization == null) {
                return;
            }

            var translation = new OnePieceSerieTranslationDTO();

            translation.setLocalization(localization);
            translation.setName(serieName);
            translations.put(localization, translation);
        });
        translations.computeIfPresent(Localization.JAPAN, (kjp, jp) -> { // TODO remove when we cange this behavior
            translations.computeIfPresent(Localization.USA, (kus, us) -> {
                jp.setName(StringUtils.defaultIfBlank(us.getName(), jp.getName()));
                return us;
            });
            return jp;
        });
        return serie;
    }

    @Cacheable
    public List<OnePieceSetDTO> getSets() {
        return getGroupedSets().values().stream()
                .map(this::createSet)
                .filter(Objects::nonNull)
                .toList();
    }

    @Nonnull
    private Map<String, List<OnePieceOfficialSiteSet>> getGroupedSets() {
        return onePieceOfficialSiteParser.getSets().stream()
                .collect(Collectors.groupingBy(OnePieceOfficialSiteSet::code));
    }

    public Optional<OnePieceSetDTO> findSetByCode(String code) {
        return getSets().stream()
                .filter(s -> StringUtils.equalsIgnoreCase(s.getCode(), code))
                .findFirst();
    }

    private OnePieceSetDTO createSet(List<OnePieceOfficialSiteSet> sets) {
        if (CollectionUtils.isEmpty(sets)) {
            return null;
        }

        var set = onePieceOfficialSiteMapper.mapToSetDTO(sets);

        if (set == null) {
            return null;
        }

        var translations = set.getTranslations();

        sets.stream()
                .map(s -> onePieceSerieService.findByName(s.serie()))
                .<OnePieceSerieDTO>mapMulti(Optional::ifPresent)
                .findFirst()
                .ifPresent(serie -> set.setSerieId(serie.getId()));
        translations.computeIfPresent(Localization.JAPAN, (kjp, jp) -> { // TODO remove when we cange this behavior
            translations.computeIfPresent(Localization.USA, (kus, us) -> {
                jp.setName(StringUtils.defaultIfBlank(us.getName(), jp.getName()));
                return us;
            });
            return jp;
        });
        return set;
    }

    public List<OnePieceCardDTO> getCards(Ulid setId) {
        if (setId == null) {
            return Collections.emptyList();
        }

        return onePieceSetService.findById(setId)
                .map(onePieceSetDTO -> {
                    var promo = onePieceSetDTO.isPromo();

                    return onePieceSetDTO.getOfficialSiteIds().stream()
                            .<OnePieceOfficialSiteCard>mapMulti((i, downstream) -> onePieceOfficialSiteParser.getCards(i.localization(), i.id(), promo).forEach(downstream))
                            .collect(groupMatching(promo)).stream()
                            .map(this::createCard)
                            .filter(Objects::nonNull)
                            .toList();
                })
                .orElse(Collections.emptyList());

    }

    @Nonnull
    private static Collector<OnePieceOfficialSiteCard, List<List<OnePieceOfficialSiteCard>>, List<List<OnePieceOfficialSiteCard>>> groupMatching(boolean promo) {
        return PCAUtils.groupMatching((c1, c2) -> {
            if (!StringUtils.equalsIgnoreCase(c1.number(), c2.number())) {
                return false;
            } else if (c1.parallel() == 0 && c2.parallel() == 0) {
                return true;
            } else if (promo) {
                return DISTRIBUTION_COMPARATOR.compare(c1, c2) == 0;
            }
            return c1.parallel() == c2.parallel();
        });
    }

    private OnePieceCardDTO createCard(List<OnePieceOfficialSiteCard> onePieceOfficialSiteCard) {
        var card = onePieceOfficialSiteMapper.mapToCardDTO(onePieceOfficialSiteCard);

        if (card == null) {
            return null;
        }

        var translations = card.getTranslations();

        translations.computeIfPresent(Localization.JAPAN, (kjp, jp) -> { // TODO remove when we cange this behavior
            translations.computeIfPresent(Localization.USA, (kus, us) -> {
                jp.setName(StringUtils.defaultIfBlank(us.getName(), jp.getName()));
                jp.setLabelName(StringUtils.defaultIfBlank(us.getLabelName(), jp.getLabelName()));
                return us;
            });
            return jp;
        });
        return card;
    }
}
