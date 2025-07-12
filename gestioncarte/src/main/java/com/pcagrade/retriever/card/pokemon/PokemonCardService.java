package com.pcagrade.retriever.card.pokemon;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.commons.FilterHelper;
import com.pcagrade.mason.jpa.revision.HistoryTreeDTO;
import com.pcagrade.mason.jpa.revision.message.RevisionMessage;
import com.pcagrade.mason.jpa.revision.message.RevisionMessageService;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.ulid.UlidHelper;
import com.pcagrade.retriever.PCAException;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.cache.CacheService;
import com.pcagrade.retriever.card.CardTranslation;
import com.pcagrade.retriever.card.artist.CardArtistService;
import com.pcagrade.retriever.card.extraction.status.CardExtractionStatusMapper;
import com.pcagrade.retriever.card.pokemon.bracket.Bracket;
import com.pcagrade.retriever.card.pokemon.bracket.BracketMapper;
import com.pcagrade.retriever.card.pokemon.bracket.BracketRepository;
import com.pcagrade.retriever.card.pokemon.feature.FeatureRepository;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetRepository;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetService;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslation;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationDTO;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationMapper;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationRepository;
import com.pcagrade.retriever.card.promo.PromoCardService;
import com.pcagrade.retriever.card.tag.CardTagMapper;
import com.pcagrade.retriever.card.tag.CardTagRepository;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.SimilarityScore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
@Transactional
@CacheConfig(cacheNames={"pokemonCardTypes"})
public class PokemonCardService implements IPokemonCardService {
	private static final Logger LOGGER = LogManager.getLogger(PokemonCardService.class);

	@Value("${retriever.name.match.threshold}")
	private double nameMatchThreshold;
	@Value("${retriever.bracket.match.threshold}")
	private double bracketMatchThreshold;

	@Autowired
	private SimilarityScore<Double> similarityScore;
	@Autowired
	private PokemonCardMapper pokemonCardMapper;
	@Autowired
	private CardExtractionStatusMapper cardExtractionStatusMapper;
	@Autowired
	private BracketMapper bracketMapper;
	@Autowired
	private PokemonCardTranslationMapper pokemonCardTranslationMapper;
	@Autowired
	private CardTagMapper cardTagMapper;
	@Autowired
	private PokemonCardRepository pokemonCardRepository;
	@Autowired
	private PokemonCardTranslationRepository pokemonCardTranslationRepository;
	@Autowired
	private BracketRepository bracketRepository;
	@Autowired
	private FeatureRepository featureRepository;
    @Autowired
	private PokemonSetRepository pokemonSetRepository;
	@Autowired
	private CardTagRepository cardTagRepository;
	@Autowired
	private PokemonSetService pokemonSetService;
	@Autowired
	private PromoCardService promoCardService;
	@Autowired
	private PokemonCardMergeService pokemonCardMergeService;
	@Autowired
	private CacheService cacheService;
	@Autowired
	private CardArtistService cardArtistService;
	@Autowired
	private RevisionMessageService revisionMessageService;

	@Override
	public List<PokemonCardDTO> findSavedCards(PokemonCardDTO card) {
		var start = Instant.now();
		var cards = doFindSavedCards(card);

		LOGGER.debug("Found {} cards in {}", cards::size, () -> PCAUtils.logDuration(start));
		return cards;
	}


	private List<PokemonCardDTO> doFindSavedCards(PokemonCardDTO card) {
		var cards = PCAUtils.progressiveFilter(findUnfilteredSavedCards(card).stream()
						.filter(c -> c.isDistribution() == card.isDistribution() && c.isAlternate() == card.isAlternate())
						.toList(),
				c -> filterBracket(card, c));

		return promoCardService.applyPromoFilter(card, cards, PokemonCardDTO::getPromos);
	}

	private boolean filterBracket(PokemonCardDTO card, PokemonCardDTO c) {
		var bulbapediaBrackets = card.getBrackets().stream()
				.filter(b -> c.getTranslations().containsKey(b.getLocalization()))
				.toList();
		var brackets = c.getBrackets();

		if (brackets.isEmpty()) {
			return CollectionUtils.isEmpty(bulbapediaBrackets);
		}
		return brackets.stream().anyMatch(b -> {
			var name = b.getName();

			return bulbapediaBrackets.stream().anyMatch(bb -> similarityScore.apply(bb.getName(), name) >= bracketMatchThreshold);
		});
	}

	private List<PokemonCardDTO> findUnfilteredSavedCards(PokemonCardDTO card) {
		List<Ulid> ids = new ArrayList<>();

		return card.getSetIds().stream()
				.filter(Objects::nonNull)
				.<PokemonCardDTO>mapMulti((setId, downstream) -> {
					try {
						pokemonSetService.getSetLocalizations(setId).forEach(l -> {
							var translation = card.getTranslations().get(l);

							if (translation == null) {
								return;
							}
							findCardsInSet(translation.getName(), translation.getNumber(), l, setId, card.isDistribution() || card.isAlternate(), false).forEach(c -> {
								if (!ids.contains(c.getId()) && isStaff(c) == isStaff(card)) {
									ids.add(c.getId());
									downstream.accept(c);
								}
							});
						});
					} catch (Exception e) {
						LOGGER.error("Error while searching for saved cards", e);
					}
				})
				.filter(FilterHelper.distinctByComparator((c1, c2) -> UlidHelper.compare(c1.getId(), c2.getId())))
				.toList();
	}

	private boolean isStaff(PokemonCardDTO card) {
		var brackets = card.getBrackets();

		if (CollectionUtils.isEmpty(brackets)) {
			return false;
		}
		return brackets.stream().anyMatch(b -> StringUtils.equalsIgnoreCase(b.getName(), PokemonCardHelper.STAFF));
	}

	@Override
	public List<PokemonCardDTO> findParentCard(PokemonCardDTO card) {
		if ((card.isDistribution() || card.isAlternate()) && card.getTranslations().containsKey(Localization.USA)) {
			var translation = card.getTranslations().get(Localization.USA);

			return card.getSetIds().stream()
					.<PokemonSetDTO>mapMulti((id, downstream) -> pokemonSetService.findSet(id).ifPresent(downstream))
					.filter(s -> s.getTranslations().containsKey(Localization.USA))
					.findAny()
					.map(s -> findParentCard(translation.getName(), translation.getNumber(), Localization.USA, s.getId())
							.filter(c -> !UlidHelper.equals(c.getId(), card.getId()))
							.toList())
					.orElse(Collections.emptyList());
		}
		return Collections.emptyList();
	}

	@Override
	public Stream<PokemonCardDTO> findParentCard(String name, String number, Localization localization, Ulid setId) {
		var split = number.split("/");

		return findCardsInSet(name, PokemonCardHelper.removeAlternate(split[0]) + (split.length > 1 ? "/" + split[1] : ""), localization, setId, false);
	}

	@Override
	public Stream<PokemonCardDTO> findCardsInSet(String name, String number, Localization localization, Ulid setId, boolean distribution) {
		return findCardsInSet(name, number, localization, setId, distribution, true);
	}

	private Stream<PokemonCardDTO> findCardsInSet(String name, String number, Localization localization, Ulid setId, boolean distribution, boolean strict) {
		var list = pokemonCardTranslationRepository.findAllByNameAndNumberAndLocalizationAndSetId(name, number, localization, setId);
		Stream<PokemonCardTranslation> stream;

		if (!strict && CollectionUtils.isEmpty(list)) {
			stream = pokemonCardTranslationRepository.findAllByNumberAndLocalizationAndSetId(number, localization, setId).stream()
					.filter(c -> similarityScore.apply(c.getName(), name) >= nameMatchThreshold || similarityScore.apply(c.getLabelName(), name) >= nameMatchThreshold);
		} else {
			stream = list.stream();
		}
		return stream
				.map(PokemonCardTranslation::getCard)
				.filter(card -> card != null && (!strict || distribution == BooleanUtils.toBoolean(card.isDistribution())))
				.map(card -> pokemonCardMapper.mapToDto(card));
	}

	@Override
	public List<PokemonCardDTO> getAllCardsInSet(Ulid setId) {
		return pokemonCardRepository.findInSet(setId).stream()
				.map(pokemonCardMapper::mapToDto)
				.sorted(Comparator.comparing(PokemonCardDTO::getIdPrim, StringUtils::compare))
				.toList();
	}

	@Override
	@RevisionMessage("Suppression de la carte pokemon {0}")
	public void deleteCard(Ulid id) {
		pokemonCardRepository.findById(id).ifPresent(this::deleteCard);
	}

	@Override
	@RevisionMessage("Suppression du contenu de l''extension {0}")
	public void deleteInSet(@Nullable Ulid setId) {
		if (setId == null) {
			return;
		}

		LOGGER.info("Clearing set {}", setId);
		pokemonCardRepository.findInSet(setId).forEach(this::deleteCard);
		pokemonCardRepository.flush();
		LOGGER.info("Cleared set {}", setId);
	}

	private void deleteCard(@Nonnull PokemonCard card) {
		if (CollectionUtils.isNotEmpty(card.getCertifications())) {
			throw new PCAException("Cannot delete card " + card.getId() + " because it has certifications");
		}
		LOGGER.info("Removing pokemon card: {}", card::getId);
		var parent = card.getParent();

		if (parent != null) {
			LOGGER.debug("Removing parent: {} from card {}", parent::getId, card::getId);
			parent.getChildren().remove(card);
			card.setParent(null);
		}
		pokemonCardRepository.delete(card);
	}

	@Override
	public synchronized void merge(PokemonCardDTO dto, List<Ulid> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			save(dto);
			return;
		}
		populateIds(dto, ids);
		var filteredIds = ids.stream()
				.filter(id -> !UlidHelper.equals(id, dto.getId()))
				.toList();

		if (CollectionUtils.isEmpty(filteredIds)) {
			var card = pokemonCardRepository.getOrCreate(dto.getId(), PokemonCard::new);

			if (Boolean.TRUE.equals(card.isFullArt())) {
				dto.setFullArt(true);
			}
			doSaveCard(card, dto);
			revisionMessageService.addMessage("Sauvegarde de la carte {0}", card.getId());
			return;
		}
		PokemonCard card = pokemonCardRepository.getOrCreate(dto.getId(), PokemonCard::new);

		var savedCard = doSaveCard(card, dto);

		var id = savedCard.getId();

		pokemonCardMergeService.merge(id, filteredIds);
	}

	private void populateIds(PokemonCardDTO dto, List<Ulid> ids) {
		if (dto.getId() == null) {
			dto.setId(ids.get(0));
		}
	}

	private PokemonCard doSaveCard(PokemonCard card, PokemonCardDTO dto) {
		if (dto.getParentId() == null && (dto.isAlternate() || dto.isDistribution())) {
			var translation = dto.getTranslations().get(Localization.USA);

			dto.getSetIds().stream()
					.flatMap(setId -> findParentCard(translation.getName(), translation.getNumber(), Localization.USA, setId))
					.findAny()
					.ifPresent(c -> dto.setParentId(c.getId()));
		}

		setChanges(card, dto);

		var savedCard = pokemonCardRepository.saveAndFlush(card);

		promoCardService.setPromosForCard(savedCard.getId(), dto.getPromos());
		cardArtistService.setCardArtist(savedCard.getId(), dto.getArtist());

		LOGGER.info("Saved card: {} ({})", savedCard::getCard, savedCard::getId);

		return pokemonCardRepository.save(savedCard);
	}

	@Override
	@RevisionMessage("Sauvegarde de la carte {return}")
	public synchronized Ulid save(PokemonCardDTO dto) {
		var id = dto.getId();
		PokemonCard card = pokemonCardRepository.getOrCreate(id, PokemonCard::new);

		return doSaveCard(card, dto).getId();
	}

	@Override
	@RevisionMessage("Reconstruction des idPrim pour l''extension {0}")
	public void rebuildIdsPrim(Ulid setId) {
		if (setId == null) {
			return;
		}

		var opt = pokemonSetRepository.findById(setId);

		if (opt.isEmpty()) {
			return;
		}
		var set = opt.get();

		var jp = set.getTranslation(Localization.JAPAN) != null;
		var localization = jp ? Localization.JAPAN : Localization.USA;
		var existing = new ArrayList<String>();

		var idPca = pokemonSetService.getIdPca(setId);
		pokemonCardRepository.findInSet(setId).stream()
				.sorted((Comparator.comparing(c -> {
					if (c.getTranslation(localization) instanceof PokemonCardTranslation cardTranslation) {
						return cardTranslation.getNumber();
					}
					return "";
				}, PCAUtils::compareTrimmed))).forEach(c -> {
					buildIdPrim(Integer.toString(idPca), c, localization, existing, new AtomicInteger(1));
					pokemonCardRepository.save(c);
				});
		pokemonCardRepository.flush();
		LOGGER.info("Rebuilt ids for set: {}", setId);
	}

	@Override
	public Optional<PokemonCardDTO> findCardWithPromo(Ulid promoId) {
		if (promoId == null) {
			return Optional.empty();
		}

		return pokemonCardRepository.findByPromoId(promoId)
				.map(pokemonCardMapper::mapToDto);
	}

	private void buildIdPrim(String idPca, PokemonCard card, Localization localization, List<String> existing, AtomicInteger lastUnnumbered) {
		var jp = localization == Localization.JAPAN;

		if (card.getTranslation(localization) instanceof PokemonCardTranslation cardTranslation) {
			var cardNum = cardTranslation.getNumber();
			var unnumbered = jp && PokemonCardHelper.isNoNumber(cardNum);


			for (int iter = unnumbered ? lastUnnumbered.get() : 0; iter < (unnumbered ? 1000 : 10); iter++) {
				StringBuilder number = new StringBuilder();

				if (unnumbered) {
					number.append(iter);
				} else if (cardNum.contains("/")) {
					number.append(cardNum.split("/")[0].replaceAll("[^\\d.]", ""));
				} else {
					number.append(cardNum.replaceAll("[^\\d.]", ""));
				}


				while (number.length() <= 2) {
					number.insert(0, '0');
				}
				if (!unnumbered) {
					number.insert(0, iter);
				}
				number.insert(0, idPca);
				number.append("00");

				var idPrim = number.toString();

				if (!existing.contains(idPrim)) {
					if (jp) {
						card.setIdPrimJp(idPrim);
						if (unnumbered) {
							lastUnnumbered.set(iter);
						}
					} else {
						card.setIdPrim(idPrim);
					}
					LOGGER.debug("Set id prim: {} for card: {}", idPrim, card.getId());
					existing.add(idPrim);
					return;
				}
			}
		}
	}

	private void setChanges(PokemonCard card, PokemonCardDTO dto) {
		if (card.getId() == null) {
			var id = dto.getId();

			card.setId(id);
		}
		card.setStatus(0);
		card.setLevel(dto.getLevel());
		card.setFullArt(dto.isFullArt());
		card.setDistribution(dto.isDistribution());
		updateTranslations(card, dto);
		if (StringUtils.isNotBlank(dto.getCard())) {
			card.setCard(dto.getCard());
		}
		if (StringUtils.isNotBlank(dto.getType())) {
			card.setType(dto.getType());
		}
		if (StringUtils.isNotBlank(dto.getType2())) {
			card.setType2(dto.getType2());
		}
		setChangesFromList(card.getBrackets(), dto.getBrackets(), b -> Optional.of(bracketRepository.getOrCreate(b.getId(), () -> {
			var bracket = bracketMapper.mapFromDto(b);

			bracket.getCards().add(card);
			cacheService.evict("bracket");
			return bracketRepository.save(bracket);
		})));
		setChangesFromList(card.getFeatures(), dto.getFeatureIds(), featureRepository::findById);
		setChangesFromList(card.getCardSets(), dto.getSetIds(), pokemonSetRepository::findById);
		setChangesFromList(card.getCardTags(), dto.getTags(), t -> Optional.of(cardTagRepository.getOrCreate(t.getId(), () -> {
			var tag = cardTagMapper.mapFromDto(t);

			tag.getCards().add(card);
			cacheService.evict("cardTags");
			return cardTagRepository.save(tag);
		})));

		updateParent(card, dto);

		card.setDistribution(card.isDistribution() || dto.isDistribution() || dto.isAlternate());
		card.setChildWithBracket(card.getParent() != null && !card.isDistribution() && !card.getBrackets().isEmpty());
		handleExtractionStatus(card, dto);
	}

	private void updateParent(PokemonCard card, PokemonCardDTO dto) {
		var parentId = dto.getParentId();

		if (parentId != null) {
			pokemonCardRepository.findByNullableId(parentId).ifPresent(card::setParent);
		} else {
			var parent = card.getParent();

			if (parent != null) {
				parent.getChildren().remove(card);
				card.setParent(null);
				pokemonCardRepository.save(parent);
			}
		}
	}

	private void handleExtractionStatus(PokemonCard card, PokemonCardDTO dto) {
		var extractionStatusDTO = dto.getExtractionStatus();

		if (extractionStatusDTO != null) {
			var extractionStatus = card.getExtractionStatus();

			if (extractionStatus == null) {
				card.setExtractionStatus(cardExtractionStatusMapper.mapFromDto(extractionStatusDTO));
			} else {
				cardExtractionStatusMapper.update(extractionStatus, extractionStatusDTO);
			}
		}
	}

	private <T, D> void setChangesFromList(Collection<? super T> to, Collection<? extends D> list, Function<? super D, Optional<? extends T>> mapper) {
		to.clear();
		list.stream()
				.filter(Objects::nonNull)
				.<T>mapMulti((d, downstream) -> mapper.apply(d).ifPresent(downstream))
				.forEach(to::add);
	}

	private void updateTranslations(PokemonCard card, PokemonCardDTO dto) {
		var dtoTranslations = dto.getTranslations();

		for (CardTranslation translation : card.getTranslations()) {
			var localization = translation.getLocalization();

			if (!dtoTranslations.containsKey(localization)) {
				card.setTranslation(localization, null);
				translation.setTranslatable(null);
				pokemonCardTranslationRepository.delete((PokemonCardTranslation) translation);
			}
		}
		dtoTranslations.forEach((localization, translationDto) -> updateTranslation(card, localization, translationDto));
		if (Boolean.TRUE.equals(card.isFullArt())) {
			for (CardTranslation translation : card.getTranslations()) {
				var name = StringUtils.trimToEmpty(translation.getName());
				var labelName = StringUtils.trimToEmpty(translation.getLabelName());

				if (!StringUtils.endsWith(name, PokemonCardHelper.FA_SUFFIX)) {
					translation.setName(name + PokemonCardHelper.FA_SUFFIX);
				}
				if (!StringUtils.endsWith(labelName, PokemonCardHelper.FA_SUFFIX)) {
					translation.setLabelName(labelName + PokemonCardHelper.FA_SUFFIX);
				}
			}
		}
	}

	private void updateTranslation(PokemonCard card, Localization localization, PokemonCardTranslationDTO translationDto) {
		if (StringUtils.isBlank(translationDto.getName())) {
			return;
		}

		CardTranslation translation = card.getTranslation(localization);

		if (StringUtils.isBlank(translationDto.getRarity())) {
			translationDto.setRarity("");
		}
		if (translation instanceof PokemonCardTranslation pokemonCardTranslation) {
			pokemonCardTranslationMapper.update(pokemonCardTranslation, translationDto);
			if (pokemonCardTranslation.getLocalization() != localization) {
				pokemonCardTranslation.setLocalization(localization);
			}
		} else {
			var t = pokemonCardTranslationMapper.mapFromDto(translationDto);

			card.setTranslation(localization, t);
			pokemonCardTranslationRepository.save(t);
		}
	}

	@Override
	@Transactional
	public Optional<PokemonCardDTO> getCardById(@Nullable Ulid id) {
		if (id == null) {
			return Optional.empty();
		}
		return pokemonCardRepository.findById(id)
				.map(pokemonCardMapper::mapToDto);
	}

	@Override
	@Transactional
	public List<PokemonCardDTO> getCardChildrenById(Ulid id) {
		return pokemonCardRepository.findAllByParentId(id).stream()
				.map(pokemonCardMapper::mapToDto)
				.toList();
	}

	@Override
	@Transactional
	public HistoryTreeDTO<PokemonCardDTO> getCardHistoryById(Ulid id) {
		return pokemonCardMergeService.getHistoryTreeById(id)
				.map(pokemonCardMapper::mapToDto);
	}

	@Override
	@Cacheable("pokemonCardTypes")
	public List<String> getAllTypes() {
		return pokemonCardRepository.findAllTypes().stream()
				.map(StringUtils::trimToEmpty)
				.filter(StringUtils::isNotBlank)
				.filter(t -> !StringUtils.equalsIgnoreCase(t, "aucun"))
				.toList();
	}

	@Override
	public List<PokemonCardDTO> findCardsWithBracket(Ulid bracketId) {
		return bracketRepository.findById(bracketId)
				.map(Bracket::getCards)
				.orElseGet(Collections::emptyList)
				.stream()
				.map(pokemonCardMapper::mapToDto)
				.toList();
	}
}
