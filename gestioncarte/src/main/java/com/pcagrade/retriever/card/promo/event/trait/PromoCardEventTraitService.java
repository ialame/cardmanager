package com.pcagrade.retriever.card.promo.event.trait;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.revision.HistoryTreeDTO;
import com.pcagrade.mason.jpa.revision.message.RevisionMessage;
import com.pcagrade.mason.ulid.UlidHelper;
import com.pcagrade.retriever.card.TradingCardGame;
import com.pcagrade.retriever.card.promo.event.PromoCardEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = { "promoEventTrait" })
public class PromoCardEventTraitService {

    @Autowired
    private PromoCardEventRepository promoCardEventRepository;
    @Autowired
    private PromoCardEventTraitRepository promoCardEventTraitRepository;
    @Autowired
    private PromoCardEventTraitMapper promoCardEventTraitMapper;
    @Autowired
    private PromoCardEventTraitMergeService promoCardEventTraitMergeService;

    @Cacheable
    public List<PromoCardEventTraitDTO> findAll() {
        return promoCardEventTraitRepository.findAll().stream()
                .map(promoCardEventTraitMapper::mapToDTO)
                .sorted(PromoCardEventTraitDTO.COMPARATOR)
                .toList();
    }

    public List<PromoCardEventTraitDTO> findAllByTcg(TradingCardGame tcg) {
        return promoCardEventTraitRepository.findAllByTcg(tcg).stream()
                .map(promoCardEventTraitMapper::mapToDTO)
                .sorted(PromoCardEventTraitDTO.COMPARATOR)
                .toList();
    }

    public Optional<PromoCardEventTraitDTO> findById(Ulid id) {
        return promoCardEventTraitRepository.findById(id)
                .map(promoCardEventTraitMapper::mapToDTO);
    }

    @Transactional
    @RevisionMessage("Sauvegarde des caracteristiques pour l''evenement promotionel {0}")
    public void setEventTraits(Ulid eventId, List<PromoCardEventTraitDTO> traits) {
        var opt = promoCardEventRepository.findById(eventId);

        if (opt.isEmpty()) {
            return;
        }

        var event = opt.get();
        var eventTraits = event.getTraits();

        eventTraits.removeIf(t -> traits.stream().noneMatch(trait -> UlidHelper.equals(t.getId(), trait.getId())));
        for (var trait : traits) {
            if (eventTraits.stream().anyMatch(t -> UlidHelper.equals(t.getId(), trait.getId()))) {
                continue;
            }

            eventTraits.add(promoCardEventTraitRepository.getOrCreate(trait.getId(), () -> promoCardEventTraitRepository.save(promoCardEventTraitMapper.mapFromDTO(trait))));
        }
        promoCardEventRepository.save(event);
    }

    @Transactional
    @RevisionMessage("Sauvegarde de la caracteristique d''evenement promotionel {return}")
    @CacheEvict(cacheNames = { "promoEventTrait" }, allEntries = true)
    public Ulid save(PromoCardEventTraitDTO dto) {
        var trait = promoCardEventTraitRepository.getOrCreate(dto.getId(), PromoCardEventTrait::new);

        promoCardEventTraitMapper.update(trait, dto);
        return promoCardEventTraitRepository.save(trait).getId();
    }

    @Transactional
    @CacheEvict(cacheNames = { "promoEventTrait" }, allEntries = true)
    public Ulid merge(PromoCardEventTraitDTO dto, List<Ulid> ids) {
        dto.setId(promoCardEventTraitMergeService.merge(dto.getId(), ids));
        return save(dto);
    }

    @Transactional
    public HistoryTreeDTO<PromoCardEventTraitDTO> getHistoryById(Ulid id) {
        return promoCardEventTraitMergeService.getHistoryTreeById(id)
                .map(promoCardEventTraitMapper::mapToDTO);
    }

}
