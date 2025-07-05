package com.pcagrade.retriever.card.yugioh;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.commons.FilterHelper;
import com.pcagrade.mason.jpa.revision.message.RevisionMessage;
import com.pcagrade.mason.jpa.revision.message.RevisionMessageService;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.ulid.UlidHelper;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.artist.CardArtistService;
import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetRepository;
import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetService;
import com.pcagrade.retriever.card.yugioh.translation.YuGiOhCardTranslation;
import com.pcagrade.retriever.card.yugioh.translation.YuGiOhCardTranslationDTO;
import com.pcagrade.retriever.card.yugioh.translation.YuGiOhCardTranslationRepository;
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
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Service
@Transactional
public class YuGiOhCardService {

    private static final Logger LOGGER = LogManager.getLogger(YuGiOhCardService.class);

    @Autowired
    private YuGiOhCardRepository yuGiOhCardRepository;
    @Autowired
    private YuGiOhSetRepository yuGiOhSetRepository;
    @Autowired
    private YuGiOhCardTranslationRepository yuGiOhCardTranslationRepository;
    @Autowired
    private YuGiOhCardMapper yuGiOhCardMapper;
    @Autowired
    private CardArtistService cardArtistService;
    @Autowired
    private YuGiOhSetService yuGiOhSetService;
    @Autowired
    private YuGiOhCardMergeService yuGiOhCardMergeService;
    @Autowired
    private RevisionMessageService revisionMessageService;

    public Optional<YuGiOhCardDTO> findById(Ulid id) {
        return yuGiOhCardRepository.findById(id)
                .map(yuGiOhCardMapper::mapToDTO);
    }

    public List<YuGiOhCardDTO> findSavedCards(YuGiOhCardDTO card) {
        return card.getTranslations().values().stream()
                .mapMulti(findSavedCardsForTranslation())
                .filter(FilterHelper.distinctByComparator(Comparator.comparing(YuGiOhCardDTO::getId, UlidHelper::compare)))
                .toList();
    }

    private BiConsumer<YuGiOhCardTranslationDTO, Consumer<YuGiOhCardDTO>> findSavedCardsForTranslation() {
        return (translation, downstream) -> yuGiOhCardTranslationRepository.findAllByLocalizationAndNumber(translation.getLocalization(), translation.getNumber())
                .stream()
                .filter(t -> StringUtils.endsWithIgnoreCase(t.getRarity(), translation.getRarity()))
                .map(t -> yuGiOhCardMapper.mapToDTO((YuGiOhCard) t.getTranslatable()))
                .forEach(downstream);
    }

    public Ulid save(YuGiOhCardDTO dto) {
        return doSave(dto);
    }

    public Ulid merge(YuGiOhCardDTO dto, List<Ulid> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return doSave(dto);
        }
        if (dto.getId() == null) {
            dto.setId(ids.remove(0));
        }
        if (CollectionUtils.isEmpty(ids)) {
            return doSave(dto);
        }

        dto.setId(yuGiOhCardMergeService.merge(dto.getId(), ids));
        return doSave(dto);
    }

    private Ulid doSave(YuGiOhCardDTO dto) {
        var card = yuGiOhCardRepository.findByNullableId(dto.getId())
                .orElseGet(YuGiOhCard::new);

        yuGiOhCardMapper.update(card, dto);

        dto.getSetIds().forEach(id -> {
            if (card.getCardSets().stream().anyMatch(s -> UlidHelper.equals(s.getId(), id))) {
                return;
            }
            yuGiOhSetRepository.findById(id).ifPresent(s -> card.getCardSets().add(s));
        });
        card.getCardSets().removeIf(s -> !dto.getSetIds().contains(s.getId()));

        var id = yuGiOhCardRepository.saveAndFlush(card).getId();

        cardArtistService.setCardArtist(id, dto.getArtist());
        revisionMessageService.addMessage("Sauvegarde de la carte {0}", card.getId());
        return id;
    }

    public List<YuGiOhCardDTO> findAllCardsInSet(Ulid setId) {
        return yuGiOhCardRepository.findInSet(setId)
                .stream()
                .map(yuGiOhCardMapper::mapToDTO)
                .sorted(Comparator.comparing(YuGiOhCardDTO::getIdPrim, StringUtils::compare))
                .toList();
    }

    @RevisionMessage("Reconstruction des idPrim pour l''extension {0}")
    public void rebuildIdsPrim(Ulid setId) {
        if (setId == null) {
            return;
        }

        var opt = yuGiOhSetRepository.findById(setId);

        if (opt.isEmpty()) {
            return;
        }
        var set = opt.get();

        var jp = set.getTranslation(Localization.JAPAN) != null;
        var localization = jp ? Localization.JAPAN : Localization.USA;
        var existing = new ArrayList<String>();

        var idPca = yuGiOhSetService.getIdPca(setId);
        yuGiOhCardRepository.findInSet(setId).stream()
                .sorted((Comparator.comparing(c -> {
                    if (c.getTranslation(localization) instanceof YuGiOhCardTranslation cardTranslation) {
                        return cardTranslation.getNumber();
                    }
                    return "";
                }, PCAUtils::compareTrimmed))).forEach(c -> {
                    buildIdPrim(Integer.toString(idPca), c, localization, existing);
                    yuGiOhCardRepository.save(c);
                });
        yuGiOhCardRepository.flush();
        LOGGER.info("Rebuilt ids for set: {}", setId);
    }

    private void buildIdPrim(String idPca, YuGiOhCard card, Localization localization, List<String> existing) {
        if (!(card.getTranslation(localization) instanceof YuGiOhCardTranslation cardTranslation)) {
            return;
        }

        var cardNum = YuGiOhCardHelper.extractCardNumber(cardTranslation.getNumber());

        for (int iter = 0; iter < 10; iter++) {
            StringBuilder number = new StringBuilder();

            number.append(cardNum.replaceAll("[^\\d.]", ""));


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
