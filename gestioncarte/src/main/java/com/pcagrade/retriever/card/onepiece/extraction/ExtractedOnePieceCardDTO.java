package com.pcagrade.retriever.card.onepiece.extraction;

import com.pcagrade.retriever.card.onepiece.OnePieceCardDTO;
import com.pcagrade.retriever.extraction.AbstractExtractedDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

public class ExtractedOnePieceCardDTO extends AbstractExtractedDTO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private OnePieceCardDTO card;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<OnePieceCardDTO> savedCards = new ArrayList<>();

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean reviewed = false;


    public OnePieceCardDTO getCard() {
        return card;
    }

    public void setCard(OnePieceCardDTO card) {
        this.card = card;
    }

    public List<OnePieceCardDTO> getSavedCards() {
        return savedCards;
    }

    public void setSavedCards(List<OnePieceCardDTO> savedCards) {
        this.savedCards = savedCards;
    }

    public boolean isReviewed() {
        return reviewed;
    }

    public void setReviewed(boolean reviewed) {
        this.reviewed = reviewed;
    }
}
