package com.pcagrade.retriever.card.pokemon.extraction;

import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction.BulbapediaExtractionStatus;
import com.pcagrade.retriever.card.pokemon.translation.SourcedPokemonCardTranslationDTO;
import com.pcagrade.retriever.extraction.AbstractExtractedDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ExtractedPokemonCardDTO extends AbstractExtractedDTO {

    public static final Comparator<ExtractedPokemonCardDTO> CHANGES_COMPARATOR = Comparator.comparing(ExtractedPokemonCardDTO::getCard, PokemonCardDTO.CHANGES_COMPARATOR);

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private PokemonCardDTO rawExtractedCard;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private PokemonCardDTO card;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<PokemonCardDTO> savedCards = new ArrayList<>();
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<PokemonCardDTO> parentCards = new ArrayList<>();
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<SourcedPokemonCardTranslationDTO> translations = new ArrayList<>();
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private BulbapediaExtractionStatus bulbapediaStatus;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean reviewed = false;

    public PokemonCardDTO getCard() {
        return card;
    }

    public void setCard(PokemonCardDTO card) {
        this.card = card;
    }

    public List<PokemonCardDTO> getSavedCards() {
        return savedCards;
    }

    public void setSavedCards(List<PokemonCardDTO> savedCards) {
        this.savedCards = savedCards;
    }

    public List<PokemonCardDTO> getParentCards() {
        return parentCards;
    }

    public void setParentCards(List<PokemonCardDTO> parentCards) {
        this.parentCards = parentCards;
    }

    public List<SourcedPokemonCardTranslationDTO> getTranslations() {
        return translations;
    }

    public void setTranslations(List<SourcedPokemonCardTranslationDTO> translations) {
        this.translations = translations;
    }

    public BulbapediaExtractionStatus getBulbapediaStatus() {
        return bulbapediaStatus;
    }

    public void setBulbapediaStatus(BulbapediaExtractionStatus bulbapediaStatus) {
        this.bulbapediaStatus = bulbapediaStatus;
    }

    public boolean isReviewed() {
        return reviewed;
    }

    public void setReviewed(boolean reviewed) {
        this.reviewed = reviewed;
    }

    public PokemonCardDTO getRawExtractedCard() {
        return rawExtractedCard;
    }

    public void setRawExtractedCard(PokemonCardDTO rawExtractedCard) {
        this.rawExtractedCard = rawExtractedCard;
    }
}
