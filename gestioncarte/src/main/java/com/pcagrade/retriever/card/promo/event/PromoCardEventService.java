package com.pcagrade.retriever.card.promo.event;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.revision.message.RevisionMessage;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTraitService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.SimilarityScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class PromoCardEventService {

    private static final Pattern TEMPORAL_TOKENS_PATTERN = Pattern.compile(".*(\\d{4}|january|february|march|april|may|june|july|august|september|october|november|december).*", Pattern.CASE_INSENSITIVE);

    @Value("${retriever.promo.event.match.threshold}")
    private double promoEventMatchThreshold;

    @Autowired
    private SimilarityScore<Double> similarityScore;
    @Autowired
    private PromoCardEventRepository promoCardEventRepository;
    @Autowired
    private PromoCardEventMapper promoCardEventMapper;
    @Autowired
    private PromoCardEventTraitService promoCardEventTraitService;

    public Optional<PromoCardEventDTO> findById(Ulid id) {
        return promoCardEventRepository.findById(id)
                .map(promoCardEventMapper::mapToDTO);
    }

    public List<PromoCardEventDTO> findAll() {
        return promoCardEventRepository.findAll()
                .stream()
                .map(promoCardEventMapper::mapToDTO)
                .toList();
    }

    @Transactional
    public List<PromoCardEventDTO> findByNameAndSetIds(String name, List<Ulid> setIds) {
        return findByNameAndSetIds(name, setIds.toArray(Ulid[]::new));
    }

    @Transactional
    public List<PromoCardEventDTO> findByNameAndSetIds(String name, Ulid... setIds) {
        var cleanName = PromoCardHelper.cleanPromoName(name);

        return PCAUtils.progressiveFilter(promoCardEventRepository.findAll(Specification.where(PromoCardEventSpecifications.hasPromoInSets(setIds))),
                e -> StringUtils.equals(cleanName, e.getName())).stream()
                .filter(e -> similarityScore.apply(e.getName(), cleanName) >= promoEventMatchThreshold && Objects.equals(findTemporalTokens(e.getName()), findTemporalTokens(cleanName)))
                .map(promoCardEventMapper::mapToDTO)
                .toList();
    }

    private List<String> findTemporalTokens(String name) {
        var matcher = TEMPORAL_TOKENS_PATTERN.matcher(name);
        var list = new ArrayList<String>(10);

        while (matcher.find()) {
            list.add(matcher.group(1));
        }
        return List.copyOf(list);
    }

    @Transactional
    @RevisionMessage("Sauvegarde de l''evenement promotionel {return}")
    public Ulid save(PromoCardEventDTO dto) {
        var event = promoCardEventRepository.getOrCreate(dto.getId(), PromoCardEvent::new);

        promoCardEventMapper.update(event, dto);
        var id = promoCardEventRepository.save(event).getId();

        promoCardEventTraitService.setEventTraits(id, dto.getTraits());
        return id;
    }

    @Deprecated(forRemoval = true)
    public LocalDate findOldDate(Ulid id) {
        return promoCardEventRepository.findById(id)
                .map(PromoCardEvent::getReleaseDate)
                .map(LocalDate::from)
                .orElse(null);
    }
}
