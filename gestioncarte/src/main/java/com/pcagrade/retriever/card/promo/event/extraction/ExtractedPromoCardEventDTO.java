package com.pcagrade.retriever.card.promo.event.extraction;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.TradingCardGame;
import com.pcagrade.retriever.card.promo.PromoCardDTO;
import com.pcagrade.retriever.card.promo.event.PromoCardEventDTO;
import com.pcagrade.retriever.extraction.AbstractExtractedDTO;

import java.util.Comparator;
import java.util.List;

public class ExtractedPromoCardEventDTO extends AbstractExtractedDTO {

    public static final Comparator<ExtractedPromoCardEventDTO> CHANGES_COMPARATOR = Comparator.comparing(ExtractedPromoCardEventDTO::getEvent, PromoCardEventDTO.CHANGES_COMPARATOR);
    private PromoCardEventDTO event;
    private List<PromoCardEventDTO> savedEvents;
    private List<PromoCardDTO> promos;
    private List<PromoCardDTO> existingPromos;
    private List<Ulid> setIds;
    private TradingCardGame tcg;


    public PromoCardEventDTO getEvent() {
        return event;
    }

    public void setEvent(PromoCardEventDTO event) {
        this.event = event;
    }

    public List<PromoCardDTO> getPromos() {
        return promos;
    }

    public void setPromos(List<PromoCardDTO> promos) {
        this.promos = promos;
    }

    public List<PromoCardDTO> getExistingPromos() {
        return existingPromos;
    }

    public void setExistingPromos(List<PromoCardDTO> existingPromos) {
        this.existingPromos = existingPromos;
    }

    public List<Ulid> getSetIds() {
        return setIds;
    }

    public void setSetIds(List<Ulid> setIds) {
        this.setIds = setIds;
    }

    public List<PromoCardEventDTO> getSavedEvents() {
        return savedEvents;
    }

    public void setSavedEvents(List<PromoCardEventDTO> savedEvents) {
        this.savedEvents = savedEvents;
    }

    public TradingCardGame getTcg() {
        return tcg;
    }

    public void setTcg(TradingCardGame tcg) {
        this.tcg = tcg;
    }
}
