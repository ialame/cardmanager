package com.pcagrade.retriever.card.pokemon.set.extraction.status;

import com.pcagrade.mason.localization.Localization;

import java.util.List;

public class PokemonSetExtractionStatusDTO {

    private List<Localization> ignoredLocalizations;

    public List<Localization> getIgnoredLocalizations() {
        return ignoredLocalizations;
    }

    public void setIgnoredLocalizations(List<Localization> ignoredLocalizations) {
        this.ignoredLocalizations = ignoredLocalizations;
    }
}
