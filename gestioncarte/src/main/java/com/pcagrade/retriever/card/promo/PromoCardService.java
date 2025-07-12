package com.pcagrade.retriever.card.promo;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.revision.message.RevisionMessage;
import com.pcagrade.mason.ulid.UlidHelper;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.Card;
import com.pcagrade.retriever.card.CardRepository;
import com.pcagrade.retriever.card.TradingCardGame;
import com.pcagrade.retriever.card.lorcana.LorcanaCard;
import com.pcagrade.retriever.card.onepiece.OnePieceCard;
import com.pcagrade.retriever.card.pokemon.PokemonCard;
import com.pcagrade.retriever.card.pokemon.PokemonCardHelper;
import com.pcagrade.retriever.card.promo.event.PromoCardEventRepository;
import com.pcagrade.retriever.card.promo.version.PromoCardVersionRepository;
import com.pcagrade.retriever.card.yugioh.YuGiOhCard;
import jakarta.annotation.Nullable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.SimilarityScore;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PromoCardService {

    @Value("${retriever.promo.match.threshold}")
    private double promoMatchThreshold;

    @Autowired
    private SimilarityScore<Double> similarityScore;

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private PromoCardRepository promoCardRepository;
    @Autowired
    private PromoCardVersionRepository promoCardVersionRepository;
    @Autowired
    private PromoCardEventRepository promoCardEventRepository;
    @Autowired
    private PromoCardMapper promoCardMapper;

    public List<PromoCardDTO> getPromosForCard(Ulid cardId) {
        return promoCardRepository.findAllByCardId(cardId).stream()
                .map(promoCardMapper::mapToDTO)
                .toList();
    }

    public Optional<PromoCardDTO> findById(Ulid id) {
        return promoCardRepository.findById(id)
                .map(promoCardMapper::mapToDTO);
    }

    public List<PromoCardDTO> getPromosForEvent(Ulid eventId) {
        return promoCardRepository.findAllByEventId(eventId).stream()
                .map(promoCardMapper::mapToDTO)
                .toList();
    }

    @Transactional
    public List<PromoCardDTO> getPromosWithoutEvent() {
        return promoCardRepository.findAllByEventIdIsNull().stream()
                .map(p -> {
                    var promo = promoCardMapper.mapToDTO(p);
                    var tcg = getTcg(p);

                    if (promo.getVersionId() == null) {
                        promo.setVersionId(this.findVersionId(promo, tcg));
                    }
                    return promo;
                })
                .toList();
    }

    @Transactional
    @RevisionMessage("Sauvegarde des promos pour la carte {0}")
    public void setPromosForCard(Ulid cardId, List<PromoCardDTO> promos) {
        var opt = cardRepository.findByNullableId(cardId);

        if (opt.isEmpty()) {
            return;
        }

        var card = opt.get();
        var promoUsed = card.getPromoUsed();
        var promosToRemove = new HashSet<>(promoCardRepository.findAllByCardId(cardId));
        var newPromos = new HashSet<PromoCard>(promosToRemove.size() + promos.size());

        for (var dto : promos) {
            var promo = promosToRemove.stream()
                    .filter(p -> shouldMergePromos(p, dto))
                    .findFirst()
                    .orElseGet(PromoCard::new);

            update(dto, promo);
            processVersion(promo);
            newPromos.add(promo);
            promo.setCard(card);
            updatePromoUsed(promoUsed, dto, promo);
            if (promo.getId() != null) {
                promosToRemove.removeIf(p -> UlidHelper.equals(p.getId(), promo.getId()));
            }
        }
        promoCardRepository.deleteAll(promosToRemove);
        card.setPromoCards(newPromos);

        addMissingPromoUsed(card, newPromos);
        cardRepository.save(card);
        promoCardRepository.saveAll(newPromos);
    }

    private static void updatePromoUsed(Set<PromoCard> promoUsed, PromoCardDTO dto, PromoCard promo) {
        if (dto.isUsed() && promoUsed.stream().noneMatch(p -> UlidHelper.equals(p.getId(), promo.getId()))) {
            promoUsed.add(promo);
        } else if (!dto.isUsed()) {
            promoUsed.removeIf(p -> UlidHelper.equals(p.getId(), promo.getId()));
        }
    }

    private static void addMissingPromoUsed(Card card, HashSet<PromoCard> promos) {
        var promoUsed = card.getPromoUsed();

        promos.stream().collect(Collectors.groupingBy(PromoCard::getLocalization)).forEach((l, list) -> {
            if (list.size() == 1 && promoUsed.stream().noneMatch(p -> p.getLocalization() == l)) {
                promoUsed.add(list.iterator().next());
            }
        });
    }

    @Transactional
    @RevisionMessage("Sauvegarde de la promo {return}")
    public Ulid save(PromoCardDTO dto) {
        var promo = promoCardRepository.getOrCreate(dto.getId(), PromoCard::new);

        update(dto, promo);
        return promoCardRepository.save(promo).getId();
    }

    private void update(PromoCardDTO dto, PromoCard promo) {
        promoCardMapper.update(promo, dto);
        promoCardEventRepository.findByNullableId(dto.getEventId()).ifPresent(promo::setEvent);
        promoCardVersionRepository.findByNullableId(dto.getVersionId()).ifPresent(promo::setVersion);
    }

    private boolean shouldMergePromos(PromoCard promo, PromoCardDTO dto) {
        var id1 = promo.getId();
        var id2 = dto.getId();

        if (id1 != null && id2 != null) {
            return UlidHelper.equals(id1, id2);
        }
        return comparePromoNames(promo.getName(), dto.getName()) && promo.getLocalization() == dto.getLocalization();
    }

    private void processVersion(PromoCard promo) {
        if (StringUtils.isBlank(promo.getName())) {
            return;
        }

        var versions = promoCardVersionRepository.findAll();

        for (var version : versions) {
            if (StringUtils.contains(promo.getName(), version.getName())) {
                promo.setVersion(version);
                version.getPromoCards().add(promo);
                break;
            }
        }
    }

    public Ulid findVersionId(PromoCardDTO promo, TradingCardGame tcg) {
        if (StringUtils.isBlank(promo.getName())) {
            return null;
        }

        var versions = promoCardVersionRepository.findAllByTcg(tcg);

        for (var version : versions) {
            if (StringUtils.containsIgnoreCase(promo.getName(), version.getName())) {
                return version.getId();
            }
        }
        return null;
    }

    public String getPromoNameWithoutVersion(Ulid promoId) {
        return this.findById(promoId)
                .map(this::getPromoNameWithoutVersion)
                .orElse("");
    }

    public String getPromoNameWithoutVersion(PromoCardDTO promo) {
        var name = PCAUtils.clean(StringUtils.substringBefore(promo.getName(), "—"));
        var versionId = promo.getVersionId();

        if (StringUtils.isBlank(name)) {
            return "";
        } else if (versionId == null) {
            return name;
        }

        return promoCardVersionRepository.findById(versionId)
                .map(v -> PCAUtils.clean(StringUtils.replaceIgnoreCase(name, v.getName(), "")))
                .orElse(name);
    }

    @Transactional
    public List<Ulid> getSetIds(PromoCardDTO promo) {
        return getSetIds(promo.getId());
    }

    @Transactional
    public List<Ulid> getSetIds(Ulid promoId) {
        var opt = promoCardRepository.findById(promoId);

        if (opt.isEmpty()) {
            return Collections.emptyList();
        }

        var promo = opt.get();
        var localization = promo.getLocalization();

        return promo.getCard().getCardSets().stream()
                .filter(cs -> cs.getTranslation(localization) != null)
                .map(AbstractUlidEntity::getId)
                .toList();
    }

    @Transactional
    public TradingCardGame getTcg(Ulid promoId) {
        return promoCardRepository.findById(promoId)
                .map(this::getTcg)
                .orElseThrow(() -> getTcgException(promoId));
    }

    private TradingCardGame getTcg(PromoCard promo) {
        var cardClass = Hibernate.getClass(promo.getCard());

        if (PokemonCard.class.isAssignableFrom(cardClass)) {
            return TradingCardGame.POKEMON;
        } else if (YuGiOhCard.class.isAssignableFrom(cardClass)) {
            return TradingCardGame.YUGIOH;
        } else if (OnePieceCard.class.isAssignableFrom(cardClass)) {
            return TradingCardGame.ONE_PIECE;
        } else if (LorcanaCard.class.isAssignableFrom(cardClass)) {
            return TradingCardGame.LORCANA;
        }
        throw getTcgException(promo.getId());
    }

    private IllegalArgumentException getTcgException(Ulid promoId) {
        return new IllegalArgumentException("Could not find TCG for promo " + promoId);
    }

    @Nullable
    public <T> List<T> applyPromoFilter(T card, List<T> cards, Function<T, ? extends Collection<PromoCardDTO>> promoGetter) {
        var promos = promoGetter.apply(card);

        if (CollectionUtils.isNotEmpty(promos) && CollectionUtils.isNotEmpty(cards)) {
            List<T> cardsWithPromo = new ArrayList<>();

            for (var c : cards) {
                var savedCardPromos = promoGetter.apply(c);

                if (savedCardPromos.isEmpty() || savedCardPromos.stream()
                        .map(PromoCardDTO::getName)
                        .anyMatch(n -> promos.stream().anyMatch(p2 -> p2 != null && comparePromoNames(n, p2.getName())))) {
                    cardsWithPromo.add(c);
                }
            }
            return cardsWithPromo;
        }
        return cards;
    }

    private boolean comparePromoNames(String name1, String name2) {
        name1 = StringUtils.trimToEmpty(name1);
        name2 = StringUtils.trimToEmpty(name2);

        return StringUtils.containsIgnoreCase(name1, PokemonCardHelper.STAFF) == StringUtils.containsIgnoreCase(name2, PokemonCardHelper.STAFF) && similarityScore.apply(name1, name2) >= promoMatchThreshold;
    }
}
