package com.pcagrade.retriever.card.onepiece;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.revision.message.RevisionMessage;
import com.pcagrade.mason.jpa.revision.message.RevisionMessageService;
import com.pcagrade.mason.ulid.UlidHelper;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.Card;
import com.pcagrade.retriever.card.artist.CardArtistService;
import com.pcagrade.retriever.card.onepiece.set.OnePieceSetRepository;
import com.pcagrade.retriever.card.onepiece.set.OnePieceSetService;
import com.pcagrade.retriever.card.promo.PromoCardService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OnePieceCardService {

    private static final Logger LOGGER = LogManager.getLogger(OnePieceCardService.class);

    @Autowired
    private OnePieceCardRepository onePieceCardRepository;
    @Autowired
    private OnePieceSetRepository onePieceSetRepository;
    @Autowired
    private OnePieceCardMapper onePieceCardMapper;
    @Autowired
    private CardArtistService cardArtistService;
    @Autowired
    private OnePieceCardMergeService onePieceCardMergeService;
    @Autowired
    private OnePieceSetService onePieceSetService;
    @Autowired
    private PromoCardService promoCardService;
    @Autowired
    private RevisionMessageService revisionMessageService;

    public Optional<OnePieceCardDTO> findById(Ulid id) {
        return onePieceCardRepository.findById(id)
                .map(onePieceCardMapper::mapToDTO);
    }

    public List<OnePieceCardDTO> findSavedCard(OnePieceCardDTO dto) {
        if (dto == null || CollectionUtils.isEmpty(dto.getSetIds())) {
            return List.of();
        }

        var number = dto.getNumber();
        var parallel = dto.getParallel();
        var setId = dto.getSetIds().get(0);

        var cards = PCAUtils.progressiveFilter(onePieceCardRepository.findAllBySetIdAndNumber(setId, number).stream()
                                .filter(c -> (parallel == 0) == (c.getParallel() == 0))
                                .toList(),
                        c -> c.getParallel() == parallel).stream()
                .map(onePieceCardMapper::mapToDTO)
                .toList();

        return promoCardService.applyPromoFilter(dto, cards, OnePieceCardDTO::getPromos);
    }

    public List<OnePieceCardDTO> findAllInSet(Ulid setId) {
        return onePieceCardRepository.findInSet(setId)
                .stream()
                .map(onePieceCardMapper::mapToDTO)
                .sorted(Comparator.comparing(OnePieceCardDTO::getIdPrim, StringUtils::compare))
                .toList();
    }

    public Optional<OnePieceCardDTO> findCardWithPromo(Ulid promoId) {
        if (promoId == null) {
            return Optional.empty();
        }

        return onePieceCardRepository.findByPromoId(promoId)
                .map(onePieceCardMapper::mapToDTO);
    }

    @RevisionMessage("Sauvegarde de la carte one piece {return}")
    public Ulid save(OnePieceCardDTO dto) {
        return doSave(dto);
    }

    public Ulid merge(OnePieceCardDTO dto, List<Ulid> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return doSave(dto);
        }
        if (dto.getId() == null) {
            dto.setId(ids.remove(0));
        }
        if (CollectionUtils.isEmpty(ids)) {
            return doSave(dto);
        }

        dto.setId(onePieceCardMergeService.merge(dto.getId(), ids));
        return doSave(dto);
    }

    private Ulid doSave(OnePieceCardDTO dto) {
        var card = onePieceCardRepository.getOrCreate(dto.getId(), OnePieceCard::new);

        onePieceCardMapper.update(card, dto);

        dto.getSetIds().forEach(id -> {
            if (card.getCardSets().stream().anyMatch(s -> UlidHelper.equals(s.getId(), id))) {
                return;
            }
            onePieceSetRepository.findById(id).ifPresent(s -> card.getCardSets().add(s));
        });
        card.getCardSets().removeIf(s -> !dto.getSetIds().contains(s.getId()));

        var id = onePieceCardRepository.saveAndFlush(card).getId();

        cardArtistService.setCardArtist(id, dto.getArtist());
        promoCardService.setPromosForCard(id, dto.getPromos());
        revisionMessageService.addMessage("Sauvegarde de la carte {0}", card.getId());
        return id;
    }

    @RevisionMessage("Reconstruction des idPrim pour l''extension {0}")
    public void rebuildIdsPrim(Ulid setId) {
        if (setId == null) {
            return;
        }

        var existing = new ArrayList<String>();

        var idPca = onePieceSetService.getIdPca(setId);
        onePieceCardRepository.findInSet(setId).stream()
                .sorted((Comparator.comparing(Card::getNumber, PCAUtils::compareTrimmed))).forEach(c -> {
                    buildIdPrim(Integer.toString(idPca), c, existing);
                    onePieceCardRepository.save(c);
                });
        onePieceCardRepository.flush();
        LOGGER.info("Rebuilt ids for set: {}", setId);
    }

    private void buildIdPrim(String idPca, OnePieceCard card, List<String> existing) {
        var cardNum = card.getNumber();

        for (int iter = 0; iter < 10; iter++) {
            StringBuilder number = new StringBuilder();

            if (cardNum.contains("-")) {
                number.append(cardNum.split("-")[0].replaceAll("[^\\d.]", ""));
            } else {
                number.append(cardNum.replaceAll("[^\\d.]", ""));
            }


            while (number.length() <= 2) {
                number.insert(0, '0');
            }
            number.insert(0, idPca);
            number.append("00");

            var idPrim = number.toString();

            if (!existing.contains(idPrim)) {
                card.setIdPrim(idPrim);
                LOGGER.debug("Set id prim: {} for card: {}", idPrim, card.getId());
                existing.add(idPrim);
                return;
            }
        }
    }
}
