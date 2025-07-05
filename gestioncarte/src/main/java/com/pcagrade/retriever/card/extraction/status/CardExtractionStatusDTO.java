package com.pcagrade.retriever.card.extraction.status;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pcagrade.mason.localization.Localization;

import java.util.List;

@JsonSerialize(as = CardExtractionStatusDTO.class)
public class CardExtractionStatusDTO {

    private List<Localization> ignoredLocalizations;

    public List<Localization> getIgnoredLocalizations() {
        return ignoredLocalizations;
    }

    public void setIgnoredLocalizations(List<Localization> ignoredLocalizations) {
        this.ignoredLocalizations = ignoredLocalizations;
    }
}
