package com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.commons.FilterHelper;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.ulid.UlidHelper;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.PokemonCardHelper;
import com.pcagrade.retriever.card.pokemon.PokemonCardMapper;
import com.pcagrade.retriever.card.pokemon.image.IPokemonCardImageExtractor;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetService;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.BulbapediaException;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.BulbapediaPokemonCard;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.BulbapediaPokemonCardPage2;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.ExtractedBulbapediaCard;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.expansion.ExpansionBulbapediaDTO;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.expansion.ExpansionBulbapediaService;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction.handler.IBulbapediaMappingHandler;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.parser.IBulbapediaParser;
import com.pcagrade.retriever.card.pokemon.translation.IPokemonCardTranslatorFactory;
import com.pcagrade.retriever.card.pokemon.translation.ITranslationSourceUrlProvider;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationDTO;
import com.pcagrade.retriever.card.promo.PromoCardDTO;
import com.pcagrade.retriever.image.ExtractedImageDTO;
import com.pcagrade.retriever.localization.translation.ITranslator;
import com.pcagrade.retriever.progress.IProgressTracker;
import com.pcagrade.retriever.transaction.TransactionService;
import jakarta.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.similarity.SimilarityScore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Service
@Transactional
public class BulbapediaExtractionService implements IPokemonCardTranslatorFactory, IPokemonCardImageExtractor {

    public static final String NAME = "bulbapedia";

    private static final Logger LOGGER = LogManager.getLogger(BulbapediaExtractionService.class);

    @Value("${bulbapedia.name.match.threshold}")
    private double nameMatchThreshold;

    @Autowired
    private SimilarityScore<Double> similarityScore;

    @Autowired
    private IBulbapediaParser bulbapediaParser;

    @Autowired
    private ExpansionBulbapediaService expansionBulbapediaService;

    @Autowired
    private PokemonCardMapper pokemonCardMapper;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private PokemonSetService pokemonSetService;

    @Autowired(required = false)
    private List<IBulbapediaMappingHandler> mappingHandlers;

    public List<ExtractedBulbapediaCard> getCardsBySetId(Ulid setId, boolean distribution, List<String> filters, @Nullable IProgressTracker tracker) {
        return expansionBulbapediaService.findBySetId(setId).stream()
                .<ExtractedBulbapediaCard>mapMulti((e, downstream) -> getCardsFromBulbapedia(e, distribution, filters, tracker).forEach(downstream))
                .filter(FilterHelper.distinctByComparator(ExtractedBulbapediaCard.DUPLICATE_COMPARATOR))
                .toList();
    }

    public List<ExtractedBulbapediaCard> getCardsFromBulbapedia(int expansionId, boolean distribution, List<String> filters, @Nullable IProgressTracker tracker) {
        return expansionBulbapediaService.findById(expansionId)
                .map(e -> getCardsFromBulbapedia(e, distribution, filters, tracker))
                .orElse(Collections.emptyList());
    }

    private List<ExtractedBulbapediaCard> getCardsFromBulbapedia(ExpansionBulbapediaDTO expansionBulbapedia, boolean distribution, List<String> filters, @Nullable IProgressTracker tracker) {
        var from = expansionBulbapedia.getLocalization();
        var to = PokemonCardHelper.otherLocalization(from);
        var list = getCardsFromBulbapediaPage1(expansionBulbapedia, distribution, filters);
        var size = list.size();

        if (tracker != null) {
            tracker.restart("Extraction bulbapedia (" + expansionBulbapedia.getName() + ")", size);
            LOGGER.info("{} cards found for expansion: {} ({})", () -> size, expansionBulbapedia::getName, expansionBulbapedia::getTableName);
        }

        return PCAUtils.fluxToList(Flux.fromIterable(list)
                .groupBy(c -> Pair.of(c.getName(), c.getNumber()))
                .flatMap(group -> group.collectList().flatMapMany(cards -> transactionService.executeInTransaction(s -> associateCardsInGroup(expansionBulbapedia, distribution, tracker, to, cards)))));
    }

    @Nonnull
    private Flux<ExtractedBulbapediaCard> associateCardsInGroup(ExpansionBulbapediaDTO expansionBulbapedia, boolean distribution, IProgressTracker tracker, Localization to, List<BulbapediaPokemonCard> cards) {
        var value = new TreeMap<Integer, ExtractedBulbapediaCard>();
        var start = Instant.now();
        List<ExtractedBulbapediaCard> associations = new LinkedList<>();
        var copy = new LinkedList<>(cards);
        var iterator = copy.iterator();

        while (iterator.hasNext()) {
            var card = iterator.next();

            if (tracker != null) {
                tracker.makeProgress();
            }
            LOGGER.info("Mapping card: {} {}", card::getName, card::getNumber);

            if ((distribution && !card.isAlternate()) || isPure(card)) {
                value.put(cards.indexOf(card), new ExtractedBulbapediaCard(mapCardToDTO(card, expansionBulbapedia, distribution)));
                iterator.remove();
                continue;
            }

            var page2Name  = expansionBulbapedia.getPage2Name();
            var number = card.getNumber();
            var other = PokemonCardHelper.otherLocalization(to);
            var unnumbered = isUnnumbered(other, number, expansionBulbapedia);

            if (!bulbapediaParser.isInPage2(card.getLink(), number, other, page2Name, unnumbered)) {
                value.put(cards.indexOf(card), new ExtractedBulbapediaCard(mapCardToDTO(card, expansionBulbapedia, distribution), BulbapediaExtractionStatus.NOT_IN_PAGE_2));
                iterator.remove();
                continue;
            }
            try {
                associations.addAll(getAssociationCards(card, to, page2Name, unnumbered));
            } catch (Exception e) {

                if (e instanceof BulbapediaException bulbapediaException && bulbapediaException.isHandled()) {
                    LOGGER.warn(() -> "Bulbapedia error on " + card.getName() +  " (" + card.getLink() + "): " + e.getMessage());
                } else {
                    LOGGER.error(() -> "Bulbapedia error on " + card.getName() +  " (" + card.getLink() + ")", e);
                }
                value.put(cards.indexOf(card), new ExtractedBulbapediaCard(mapCardToDTO(card, expansionBulbapedia, distribution), BulbapediaExtractionStatus.NOT_IN_PAGE_2));
                iterator.remove();
            }
        }
        associations = filterAssociation(associations);
        for (var card : copy) {
            if (CollectionUtils.isEmpty(associations)) {
                value.put(cards.indexOf(card), new ExtractedBulbapediaCard(mapCardToDTO(card, expansionBulbapedia, distribution)));
                continue;
            }
            value.put(cards.indexOf(card), associate(mapCardToDTO(card, expansionBulbapedia, distribution), associations.isEmpty() ? null :associations.get(0)));
            associations.remove(0);
        }
        LOGGER.trace("{} cards mapped in {}", cards::size, () -> PCAUtils.logDuration(start)); // TODO grafana
        return Flux.fromIterable(value.values());
    }

    private List<ExtractedBulbapediaCard> filterAssociation(List<ExtractedBulbapediaCard> sourceList) {
        return new LinkedList<>(PCAUtils.progressiveFilter(sourceList.stream()
                .filter(FilterHelper.distinctByComparator(ExtractedBulbapediaCard.DUPLICATE_COMPARATOR))
                .toList(), e -> e.status() == BulbapediaExtractionStatus.OK));
    }

    private ExtractedBulbapediaCard associate(PokemonCardDTO dto, ExtractedBulbapediaCard association) {
        var associationCard = association.card();

        dto.getTranslations().putAll(associationCard.getTranslations());
        dto.getSetIds().addAll(associationCard.getSetIds());

        var promos = Stream.concat(dto.getPromos().stream(), associationCard.getPromos().stream())
                .filter(FilterHelper.distinctByComparator(PromoCardDTO.CHANGES_COMPARATOR))
                .toList();

        if (!promos.isEmpty() && CollectionUtils.isNotEmpty(promos)) {
            promos.get(0).setUsed(true);
            dto.setPromos(promos);
        } else {
            dto.setPromos(Collections.emptyList());
        }
        dto.setBrackets(Stream.concat(dto.getBrackets().stream(), associationCard.getBrackets().stream())
                .filter(FilterHelper.distinctByComparator((b1, b2) -> UlidHelper.compare(b1.getId(), b2.getId())))
                .toList());
        dto.setTags(Stream.concat(dto.getTags().stream(), associationCard.getTags().stream())
                .filter(FilterHelper.distinctByComparator((t1, t2) -> UlidHelper.compare(t1.getId(), t2.getId())))
                .toList());
        return new ExtractedBulbapediaCard(dto, association.status());
    }

    public int getCardCountFromBulbapedia(Ulid setId, boolean distribution, List<String> filters) {
        return expansionBulbapediaService.findBySetId(setId).stream()
                .mapMulti((e, downstream) -> getCardsFromBulbapediaPage1(e, distribution, filters).forEach(downstream))
                .toList()
                .size();
    }

    @Nonnull
    private List<BulbapediaPokemonCard> getCardsFromBulbapediaPage1(ExpansionBulbapediaDTO expansionBulbapedia, boolean distribution, List<String> filters) {
        return this.getCardsFromBulbapedia(expansionBulbapedia.getUrl(),
                        distribution ? "Additional cards" : expansionBulbapedia.getTableName(),
                        expansionBulbapedia.getH3Name(),
                        distribution ? "" : expansionBulbapedia.getFirstNumber(),
                        distribution || expansionBulbapedia.isPromo()).parallelStream()
                .filter(c -> CollectionUtils.isEmpty(filters) || filters.stream().anyMatch(c::filter))
                .toList();
    }

    private List<BulbapediaPokemonCard> getCardsFromBulbapedia(String url, String tableName, String h3Name, String firstNumber, boolean promo) {
        try {
            if (StringUtils.isNotBlank(url) && StringUtils.isNotBlank(tableName)) {
                return bulbapediaParser.parseExtensionPage(StringUtils.trimToEmpty(url), StringUtils.trimToEmpty(tableName), StringUtils.trimToEmpty(h3Name), StringUtils.trimToEmpty(firstNumber), promo);
            }
        } catch (BulbapediaException e) {
            LOGGER.warn("Bulbapedia error on {}", url);
        }
        return Collections.emptyList();
    }

    private PokemonCardDTO mapCardToDTO(BulbapediaPokemonCard card, ExpansionBulbapediaDTO expansionBulbapedia, boolean distribution) {
        Localization localization = expansionBulbapedia.getLocalization();

        PokemonCardDTO dto = mapCardToDTO(card, localization);
        dto.setDistribution(distribution);
        dto.getSetIds().add(expansionBulbapedia.getSetId());
        if (localization == Localization.JAPAN && PokemonCardHelper.inUnnumbered(card.getNumber(), "")) {
            dto.getTranslations().get(Localization.JAPAN).setNumber(PokemonCardHelper.NO_NUMBER);
        }
        return dto;
    }

    private PokemonCardDTO mapCardToDTO(BulbapediaPokemonCard card, Localization localization) {
        var dto = pokemonCardMapper.mapToDto(card, localization);
        var translations = dto.getTranslations();
        var link = card.getLink();

        dto.setLevel(bulbapediaParser.findLevel(link));
        if (StringUtils.isNotBlank(link) && translations.containsKey(Localization.JAPAN)) {
            translations.get(Localization.JAPAN).setOriginalName(bulbapediaParser.findOriginalName(link));
        }
        if (CollectionUtils.isEmpty(mappingHandlers)) {
            return dto;
        }
        for (var handler : mappingHandlers) {
            try {
                handler.handle(dto, card, localization);
            } catch (Exception e) {
                LOGGER.error("Error while handling mapping with handler {}", handler.getClass().getCanonicalName(), e);
            }
        }
        return dto;
    }

    @Nonnull
    private List<ExtractedBulbapediaCard> getAssociationCards(BulbapediaPokemonCard card, Localization to, String page2Name, boolean unnumbered) {
        return bulbapediaParser.findAssociatedCards(card, to, page2Name, unnumbered).stream()
                .mapMulti(mapAssociation(card, to))
                .toList();
    }

    @Nonnull
    private BiConsumer<BulbapediaPokemonCardPage2, Consumer<ExtractedBulbapediaCard>> mapAssociation(BulbapediaPokemonCard card, Localization to) {
        return (association, downstream) -> {
            String name = association.getExpansionTableName();
            String link = association.getExpansionLink();

            if (StringUtils.containsIgnoreCase(name, "Unnumbered Promotional cards")) {
                downstream.accept(new ExtractedBulbapediaCard(mapCardToDTO(association, to), BulbapediaExtractionStatus.NOT_IN_PAGE_1));
                return;
            }

            var expansionsBulbapedia = expansionBulbapediaService.findGroup(link, name, to);

            if (CollectionUtils.isNotEmpty(expansionsBulbapedia)) {
                var distribution = association.isAlternate();
                var level = bulbapediaParser.findLevel(card.getLink());
                var forme = bulbapediaParser.findForme(card.getLink());

                for (var associationExpansionBulbapedia : expansionsBulbapedia) {
                    var number = association.getNumber();
                    var cards = PCAUtils.progressiveFilter(getPage1Associations(card, to, distribution, associationExpansionBulbapedia, number),
                            c -> StringUtils.equalsIgnoreCase(c.getType(), card.getType()),
                            c -> level == bulbapediaParser.findLevel(c.getLink()),
                            c -> StringUtils.equalsIgnoreCase(forme, bulbapediaParser.findForme(c.getLink())));

                    if (CollectionUtils.isNotEmpty(cards)) {
                        cards.forEach(c -> downstream.accept(new ExtractedBulbapediaCard(mapCardToDTO(c, associationExpansionBulbapedia, distribution))));
                    } else {
                        card.getFeatures().forEach(association::addFeature);
                        downstream.accept(new ExtractedBulbapediaCard(mapCardToDTO(association, associationExpansionBulbapedia, distribution), BulbapediaExtractionStatus.NOT_IN_PAGE_1));
                    }
                }
            } else {
                card.getFeatures().forEach(association::addFeature);
                downstream.accept(new ExtractedBulbapediaCard(mapCardToDTO(association, to), BulbapediaExtractionStatus.NO_SET));
            }
        };
    }

    @Nonnull
    private List<BulbapediaPokemonCard> getPage1Associations(BulbapediaPokemonCard card, Localization to, boolean distribution, ExpansionBulbapediaDTO expansionBulbapedia, String number) {
        var d = distribution && to == Localization.USA;
        var list = getCardsFromBulbapediaPage1(expansionBulbapedia, d, List.of(number)).stream()
                .filter(c -> isUnnumbered(to, number, expansionBulbapedia) == isUnnumbered(to, c.getNumber(), expansionBulbapedia) && similarityScore.apply(c.getName(), card.getName()) > nameMatchThreshold)
                .toList();

        if (CollectionUtils.isEmpty(list) && d) {
            list = getPage1Associations(card, to, false, expansionBulbapedia, number);
        }
        return list;
    }

    private boolean isPure(BulbapediaPokemonCard card) {
        return card.getBrackets().stream().anyMatch(PokemonCardHelper.STAFF::equalsIgnoreCase) || card.getPromos().stream().anyMatch(p -> StringUtils.containsIgnoreCase(p, PokemonCardHelper.STAFF));
    }

    @Override
    public ITranslator<PokemonCardTranslationDTO> createTranslator(PokemonSetDTO set, PokemonCardDTO card) {
        return new BulbapediaTranslator(set, card);
    }

    private boolean isUnnumbered(Localization localization, String number, ExpansionBulbapediaDTO expansionBulbapedia) {
        return localization == Localization.JAPAN && pokemonSetService.findSet(expansionBulbapedia.getSetId())
                .filter(s -> PokemonCardHelper.inUnnumbered(number, s.getTotalNumber()))
                .isPresent();
    }

    public List<ExtractedImageDTO> findImagesForExpansions(List<ExpansionBulbapediaDTO> expansions) {
        return expansions.stream()
                .<ExtractedImageDTO>mapMulti((e, downstream) -> {
                    try {
                        bulbapediaParser.findImages(e.getUrl(), e.getLocalization()).forEach(downstream);
                    } catch (WebClientResponseException ex) {
                        LOGGER.trace("Error while finding images for url {}: {}", e.getUrl(), ex.getMessage()); // already logged
                    } catch (Exception ex) {
                        LOGGER.error("Error while finding images for url {}", e.getUrl(), ex);
                    }
                })
                .distinct()
                .toList();
    }

    @Override
    public List<ExtractedImageDTO> getImages(PokemonCardDTO card, Localization localization) {
        var cardTranslation = card.getTranslations().get(localization);

        if (cardTranslation == null || !cardTranslation.isAvailable()) {
            return Collections.emptyList();
        }

        var number = cardTranslation.getNumber();

        return card.getSetIds().stream()
                .<ExpansionBulbapediaDTO>mapMulti((id, downstream) -> expansionBulbapediaService.findBySetId(id).forEach(downstream))
                .<BulbapediaPokemonCard>mapMulti((e, downstream) -> getCardsFromBulbapediaPage1(e, false, Collections.emptyList()).forEach(downstream))
                .filter(c -> StringUtils.equalsIgnoreCase(c.getNumber(), number))
                .<ExtractedImageDTO>mapMulti((c, downstream) -> bulbapediaParser.findImages(c.getLink(), localization).forEach(downstream))
                .toList();
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public byte[] getRawImage(ExtractedImageDTO image) {
        return bulbapediaParser.getImage(image.url()).block();
    }

    private class BulbapediaTranslator implements ITranslator<PokemonCardTranslationDTO>, ITranslationSourceUrlProvider {

        private final PokemonSetDTO set;
        private final PokemonCardDTO card;

        private BulbapediaTranslator(PokemonSetDTO set, PokemonCardDTO card) {
            this.set = set;
            this.card = card;
        }

        @Override
        public Optional<PokemonCardTranslationDTO> translate(PokemonCardTranslationDTO source, Localization localization) {
            if (localization == Localization.USA || localization == Localization.JAPAN) {
                if (inUnnumbered(localization, source)) {
                    var us = card.getTranslations().get(Localization.USA);

                    if (us != null) {
                        return getTranslation(localization, us.getNumber(), card.isDistribution());
                    }
                } else {
                    return getTranslation(localization, source.getNumber(), localization == Localization.USA && card.isDistribution());
                }
            }
            return Optional.empty();
        }

        private Optional<PokemonCardTranslationDTO> getTranslation(Localization localization, String number, boolean distribution) {
            return PCAUtils.progressiveFilter(card.getSetIds().stream().<PokemonCardTranslationDTO>mapMulti((setId, downstream) -> getCardsBySetId(setId, distribution, List.of(number), null).forEach(c -> {
                var translation = c.card().getTranslations().get(localization);

                if (translation != null) {
                    downstream.accept(translation.copy());
                }
            })).toList(), t -> StringUtils.endsWithIgnoreCase(t.getNumber(), number)).stream().findFirst();
        }

        private boolean inUnnumbered(Localization localization, PokemonCardTranslationDTO source) {
            return localization == Localization.JAPAN && PokemonCardHelper.inUnnumbered(source.getNumber(), set.getTotalNumber());
        }

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public String getUrl(Localization localization) {
            var link = card.getLink();

            return StringUtils.isNotBlank(link) ? link : null;
        }
    }
}
