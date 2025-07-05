package com.pcagrade.retriever.card.promo.event.extraction.handler;

import com.pcagrade.mason.commons.FilterHelper;
import com.pcagrade.mason.ulid.UlidHelper;
import com.pcagrade.retriever.card.promo.PromoCardDTO;
import com.pcagrade.retriever.card.promo.PromoCardService;
import com.pcagrade.retriever.card.promo.event.PromoCardEventDTO;
import com.pcagrade.retriever.card.promo.event.PromoCardEventService;
import com.pcagrade.retriever.card.promo.event.extraction.ExtractedPromoCardEventDTO;
import jakarta.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.stream.Stream;

public class ExistingEventHandler implements IPromoEventHandler {

    private static final Logger LOGGER = LogManager.getLogger(ExistingEventHandler.class);

    private final PromoCardService promoCardService;
    private final PromoCardEventService promoCardEventService;

    public ExistingEventHandler(PromoCardService promoCardService, PromoCardEventService promoCardEventService) {
        this.promoCardService = promoCardService;
        this.promoCardEventService = promoCardEventService;
    }

    @Override
    public void handleEvent(@Nonnull ExtractedPromoCardEventDTO dto, @Nonnull EventNameHolder nameToParse) {
        var event = dto.getEvent();
        var events = Stream.concat(promoCardEventService.findByNameAndSetIds(event.getName(), dto.getSetIds()).stream(), promoCardEventService.findByNameAndSetIds(nameToParse.toString(), dto.getSetIds()).stream())
                .filter(FilterHelper.distinctByComparator(Comparator.comparing(PromoCardEventDTO::getId, UlidHelper::compare)))
                .toList();

        if (events.size() >= 5) {
            LOGGER.warn("Found {} events for \"{}\". This is probably an error.", events::size, event::getName);
        }
        dto.setSavedEvents(events);
        if (CollectionUtils.isNotEmpty(events)) {
            event.setId(events.get(0).getId());
            dto.setExistingPromos(events.stream()
                    .<PromoCardDTO>mapMulti((e, downstream) -> promoCardService.getPromosForEvent(e.getId()).forEach(downstream))
                    .toList());
        }
    }
}
