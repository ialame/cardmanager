package com.pcagrade.retriever.card.lorcana.extraction;

import com.pcagrade.retriever.card.lorcana.LorcanaCardDTO;
import com.pcagrade.retriever.extraction.AbstractExtractedDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

public class ExtractedLorcanaCardDTO extends AbstractExtractedDTO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LorcanaCardDTO card;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<LorcanaCardDTO> savedCards = new ArrayList<>();

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean reviewed = false;


    public LorcanaCardDTO getCard() {
        return card;
    }

    public void setCard(LorcanaCardDTO card) {
        this.card = card;
    }

    public List<LorcanaCardDTO> getSavedCards() {
        return savedCards;
    }

    public void setSavedCards(List<LorcanaCardDTO> savedCards) {
        this.savedCards = savedCards;
    }

    public boolean isReviewed() {
        return reviewed;
    }

    public void setReviewed(boolean reviewed) {
        this.reviewed = reviewed;
    }
    
}
