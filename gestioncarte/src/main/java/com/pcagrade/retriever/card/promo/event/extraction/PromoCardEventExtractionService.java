package com.pcagrade.retriever.card.promo.event.extraction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import com.pcagrade.retriever.card.TradingCardGame;
import com.pcagrade.retriever.card.promo.PromoCardDTO;
import com.pcagrade.retriever.card.promo.PromoCardService;
import com.pcagrade.retriever.card.promo.event.PromoCardEventDTO;
import com.pcagrade.retriever.card.promo.event.extraction.handler.IPromoEventHandler;
import com.pcagrade.retriever.extraction.AbstractExtractionService;
import com.pcagrade.retriever.progress.IProgressTracker;
import com.pcagrade.retriever.progress.LogProgressTracker;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class PromoCardEventExtractionService extends AbstractExtractionService<ExtractedPromoCardEventDTO> {

    public static final String NAME = "promo_events";

    private static final Logger LOGGER = LogManager.getLogger(PromoCardEventExtractionService.class);

    @Autowired
    private PromoCardService promoCardService;
    @Autowired
    private List<IPromoEventHandler> promoEventHandlers;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public PromoCardEventExtractionService(ObjectMapper mapper) {
        super(NAME, mapper, ExtractedPromoCardEventDTO.class);
    }

    public List<ExtractedPromoCardEventDTO> extractEvents() {
        return this.extractEvents(Collections.emptyList());
    }

    public List<ExtractedPromoCardEventDTO> extractEvents(List<Ulid> filters) {
        return this.extractEvents(filters, null);
    }

    public List<ExtractedPromoCardEventDTO> extractEvents(List<Ulid> filters, @Nullable IProgressTracker tracker) {
        LOGGER.info("Extracting events from the database...");

        var progress = tracker != null ? tracker : new LogProgressTracker("Extraction");
        List<String> promoFilter = CollectionUtils.isEmpty(filters) ? Collections.emptyList() : filters.stream()
                .map(promoCardService::getPromoNameWithoutVersion)
                .toList();
        var map = promoCardService.getPromosWithoutEvent().stream()
                .filter(p -> (CollectionUtils.isEmpty(promoFilter) || filters.contains(p.getId()) || promoFilter.stream().anyMatch(f -> StringUtils.containsIgnoreCase(p.getName(), f))) && !StringUtils.containsIgnoreCase(p.getName(), "Battle Academy")) // FIXME handle Battle Academy later
                .collect(Collectors.groupingBy(promoCardService::getPromoNameWithoutVersion));

        progress.restart("Extraction evenements", map.size());
        var events = map.entrySet().parallelStream()
                .<ExtractedPromoCardEventDTO>mapMulti((e, downstream) -> {
                    try {
                        var dto = createExtractedEvent(e.getKey(), e.getValue());

                        progress.makeProgress();
                        downstream.accept(dto);
                    } catch (Exception ex) {
                        LOGGER.error("Error while extracting event: {}", e.getKey(), ex);
                    }
                })
                .toList();

        LOGGER.info("Extracted {} events from the database.", events.size());
        return events;
    }

    public void startExtraction(List<Ulid> filters) {
        executorService.execute(() -> this.putEvents(this.extractEvents(filters, null)));
    }

    public ExtractedPromoCardEventDTO regenerateExtractedEvent(ExtractedPromoCardEventDTO old) {
        var v = this.createExtractedEvent(old.getEvent().getName(), old.getPromos());

        v.setExistingPromos(old.getExistingPromos());
        v.setSavedEvents(old.getSavedEvents());
        this.save(v);
        return v;
    }

    // visible for testing
    ExtractedPromoCardEventDTO createExtractedEvent(String name, List<PromoCardDTO> promos) {
        if (CollectionUtils.isEmpty(promos)) {
            throw new IllegalArgumentException("Cannot extract event without promos.");
        }

        var dto = new ExtractedPromoCardEventDTO();
        var setIds = promos.stream()
                .<Ulid>mapMulti((p, downstream) -> promoCardService.getSetIds(p).forEach(downstream))
                .distinct()
                .toList();
        var event = new PromoCardEventDTO();

        event.setName(name);
        dto.setId(UlidCreator.getUlid());
        dto.setEvent(event);
        dto.setPromos(promos);
        dto.setSetIds(setIds);
        dto.setTcg(promoCardService.getTcg(promos.get(0).getId()));
        processEventName(dto);
        return dto;
    }

    private void processEventName(ExtractedPromoCardEventDTO dto) {
        var event = dto.getEvent();
        var nameToParse = new IPromoEventHandler.EventNameHolder(event.getName());

        promoEventHandlers.forEach(h -> {
            try {
                if (h.supports(dto)) {
                    h.handleEvent(dto, nameToParse);
                }
            } catch (Exception e) {
                LOGGER.error("Error while handling event name: {} with handler: {}", event.getName(), h, e);
            }
        });
    }

    public void putEvents(List<ExtractedPromoCardEventDTO> list) {
        this.put(list, ExtractedPromoCardEventDTO.CHANGES_COMPARATOR);
    }

    public List<ExtractedPromoCardEventDTO> get(TradingCardGame tcg) {
        var list = this.get();

        if (tcg != null) {
            list = list.stream()
                    .filter(e -> e.getTcg() == tcg)
                    .toList();
        }
        return list;
    }
}
