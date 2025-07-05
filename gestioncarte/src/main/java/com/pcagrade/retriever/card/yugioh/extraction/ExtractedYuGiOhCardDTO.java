package com.pcagrade.retriever.card.yugioh.extraction;

import com.pcagrade.retriever.card.yugioh.YuGiOhCardDTO;
import com.pcagrade.retriever.extraction.AbstractExtractedDTO;
import com.pcagrade.retriever.extraction.consolidation.source.SimpleConsolidationSource;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

public class ExtractedYuGiOhCardDTO extends AbstractExtractedDTO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private YuGiOhCardDTO card;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<YuGiOhCardDTO> savedCards = new ArrayList<>();

    private List<SimpleConsolidationSource<YuGiOhCardDTO>> sources = new ArrayList<>();


    public YuGiOhCardDTO getCard() {
        return card;
    }

    public void setCard(YuGiOhCardDTO card) {
        this.card = card;
    }

    public List<YuGiOhCardDTO> getSavedCards() {
        return savedCards;
    }

    public void setSavedCards(List<YuGiOhCardDTO> savedCards) {
        this.savedCards = savedCards;
    }

    public List<SimpleConsolidationSource<YuGiOhCardDTO>> getSources() {
        return sources;
    }

    public void setSources(List<SimpleConsolidationSource<YuGiOhCardDTO>> sources) {
        this.sources = sources;
    }
}
