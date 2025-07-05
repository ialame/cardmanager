package com.pcagrade.retriever.card.lorcana;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.commons.FilterHelper;
import com.pcagrade.mason.jpa.revision.message.RevisionMessage;
import com.pcagrade.mason.jpa.revision.message.RevisionMessageService;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.ulid.UlidHelper;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.artist.CardArtistService;
import com.pcagrade.retriever.card.lorcana.set.LorcanaSet;
import com.pcagrade.retriever.card.lorcana.set.LorcanaSetRepository;
import com.pcagrade.retriever.card.lorcana.transaltion.LorcanaCardTranslation;
import com.pcagrade.retriever.card.lorcana.transaltion.LorcanaCardTranslationRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LorcanaCardService {

    private static final Logger LOGGER = LogManager.getLogger(LorcanaCardService.class);

    @Autowired
    private LorcanaCardRepository lorcanaCardRepository;
    @Autowired
    private LorcanaCardTranslationRepository lorcanaCardTranslationRepository;
    @Autowired
    private LorcanaSetRepository lorcanaSetRepository;
    @Autowired
    private LorcanaCardMapper lorcanaCardMapper;
    @Autowired
    private CardArtistService cardArtistService;
    @Autowired
    private LorcanaCardMergeService lorcanaCardMergeService;
    @Autowired
    private RevisionMessageService revisionMessageService;

    public Optional<LorcanaCardDTO> findById(Ulid id) {
        return lorcanaCardRepository.findById(id)
                .map(lorcanaCardMapper::mapToDTO);
    }

    public List<LorcanaCardDTO> findAllInSet(Ulid id) {
        return lorcanaSetRepository.findById(id).stream()
                .<LorcanaCardDTO>mapMulti((set, downstream) -> set.getCards().forEach(c -> {
                    if (c instanceof LorcanaCard lorcanaCard) {
                        downstream.accept(lorcanaCardMapper.mapToDTO(lorcanaCard));
                    }
                }))
                .sorted(Comparator.comparing(LorcanaCardDTO::getIdPrim, StringUtils::compare))
                .toList();
    }

    public List<LorcanaCardDTO> findSavedCard(LorcanaCardDTO dto) {
        if (dto == null || CollectionUtils.isEmpty(dto.getSetIds())) {
            return List.of();
        }

        return dto.getTranslations().values().stream()
                .<LorcanaCardTranslation>mapMulti((t, downstream) -> PCAUtils.progressiveFilter(lorcanaCardTranslationRepository.findAllByLocalizationAndFullNumber(t.getLocalization(), t.getFullNumber()),
                                t2 -> StringUtils.equalsIgnoreCase(t.getName(), t2.getName()))
                        .forEach(downstream))
                .map(LorcanaCardTranslation::getTranslatable)
                .mapMulti(PCAUtils.cast(LorcanaCard.class))
                .filter(FilterHelper.distinctByComparator(Comparator.comparing(AbstractUlidEntity::getId, UlidHelper::compare)))
                .map(lorcanaCardMapper::mapToDTO)
                .toList();
    }

    @RevisionMessage("Sauvegarde de la carte lorcana {return}")
    public Ulid save(LorcanaCardDTO dto) {
        return doSave(dto);
    }

    public Ulid merge(LorcanaCardDTO dto, List<Ulid> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return doSave(dto);
        }
        if (dto.getId() == null) {
            dto.setId(ids.remove(0));
        }
        if (CollectionUtils.isEmpty(ids)) {
            return doSave(dto);
        }

        dto.setId(lorcanaCardMergeService.merge(dto.getId(), ids));
        return doSave(dto);
    }

    private Ulid doSave(LorcanaCardDTO dto) {
        var card = lorcanaCardRepository.getOrCreate(dto.getId(), LorcanaCard::new);

        lorcanaCardMapper.update(card, dto);

        dto.getSetIds().forEach(id -> {
            if (card.getCardSets().stream().anyMatch(s -> UlidHelper.equals(s.getId(), id))) {
                return;
            }
            lorcanaSetRepository.findById(id).ifPresent(s -> card.getCardSets().add(s));
        });
        card.getCardSets().removeIf(s -> !dto.getSetIds().contains(s.getId()));

        var id = lorcanaCardRepository.saveAndFlush(card).getId();

        cardArtistService.setCardArtist(id, dto.getArtist());
        revisionMessageService.addMessage("Sauvegarde de la carte {0}", card.getId());
        return id;
    }


    @RevisionMessage("Reconstruction des idPrim pour l''extension {0}")
    public void rebuildIdsPrim(Ulid setId) {
        if (setId == null) {
            return;
        }

        var opt = lorcanaSetRepository.findById(setId);

        if (opt.isEmpty()) {
            return;
        }
        var set = opt.get();
        var number = String.format("%03d", getSetNumber(set));

        set.getCards().forEach(c -> {
            if (c instanceof LorcanaCard card) {
                var idPrim = number + String.format("%03d", getUsNumber(card));
                card.setIdPrim(idPrim);
                LOGGER.debug("Set id prim: {} for card: {}", idPrim, card.getId());
            }
        });

        LOGGER.info("Rebuilt ids for set: {}", setId);
    }

    private int getUsNumber(LorcanaCard card) {
        try {
            var num = ((LorcanaCardTranslation) card.getTranslation(Localization.USA)).getNumber();
            var split = StringUtils.split(num, "/");

            if (split.length >= 1) {
                return Integer.parseInt(split[0]);
            }
            return Integer.parseInt(num);
        } catch (Exception e) {
            return 999;
        }

    }

    private int getSetNumber(LorcanaSet set) {
        try {
            return Integer.parseInt(set.getNumber());
        } catch (NumberFormatException e) {
            return 999;
        }
    }
}
