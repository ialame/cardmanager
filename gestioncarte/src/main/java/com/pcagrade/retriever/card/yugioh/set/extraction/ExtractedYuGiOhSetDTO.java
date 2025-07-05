package com.pcagrade.retriever.card.yugioh.set.extraction;

import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetDTO;
import com.pcagrade.retriever.extraction.AbstractExtractedDTO;
import com.pcagrade.retriever.extraction.consolidation.source.SimpleConsolidationSource;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

public class ExtractedYuGiOhSetDTO extends AbstractExtractedDTO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private YuGiOhSetDTO set;
    private List<YuGiOhSetDTO> savedSets;
    private List<SimpleConsolidationSource<YuGiOhSetDTO>> sources = new ArrayList<>();

    public YuGiOhSetDTO getSet() {
        return set;
    }

    public void setSet(YuGiOhSetDTO set) {
        this.set = set;
    }

    public List<SimpleConsolidationSource<YuGiOhSetDTO>> getSources() {
        return sources;
    }

    public void setSources(List<SimpleConsolidationSource<YuGiOhSetDTO>> sources) {
        this.sources = sources;
    }

    public List<YuGiOhSetDTO> getSavedSets() {
        return savedSets;
    }

    public void setSavedSets(List<YuGiOhSetDTO> savedSets) {
        this.savedSets = savedSets;
    }
}
