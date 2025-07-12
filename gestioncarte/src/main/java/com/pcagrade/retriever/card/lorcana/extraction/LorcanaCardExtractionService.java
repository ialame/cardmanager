package com.pcagrade.retriever.card.lorcana.extraction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.lorcana.LorcanaCardDTO;
import com.pcagrade.retriever.card.lorcana.LorcanaCardService;
import com.pcagrade.retriever.card.lorcana.source.mushu.MushuService;
import com.pcagrade.retriever.extraction.AbstractExtractionService;
import com.pcagrade.retriever.progress.ConsolidatedProgressTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class LorcanaCardExtractionService extends AbstractExtractionService<ExtractedLorcanaCardDTO> {

    public static final String NAME = "lorcana_cards";

    public static final String TRACKER_MESSAGE = "Extraction des cartes Lorcana";

    @Autowired
    private MushuService mushuService;
    @Autowired
    private LorcanaCardService lorcanaCardService;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ConsolidatedProgressTracker progressTracker = new ConsolidatedProgressTracker(TRACKER_MESSAGE);

    protected LorcanaCardExtractionService(ObjectMapper mapper) {
        super(NAME, mapper, ExtractedLorcanaCardDTO.class);
    }

    public void startExtraction(Ulid setId) {
        executorService.execute(() -> putCards(extractCards(setId)));
    }

    public List<ExtractedLorcanaCardDTO> extractCards(Ulid setId) {
        return mushuService.extractCards(setId).stream()
                .map(c -> {
                    var dto = new ExtractedLorcanaCardDTO();

                    dto.setCard(c);
                    c.setSetIds(List.of(setId));
                    dto.setSavedCards(lorcanaCardService.findSavedCard(c));
                    return dto;
                })
                .filter(dto -> dto.getSavedCards().isEmpty() || dto.getSavedCards().stream().noneMatch(c -> LorcanaCardDTO.CHANGES_COMPARATOR.compare(c, dto.getCard()) == 0))
                .toList();
    }

    public void putCards(List<ExtractedLorcanaCardDTO> list) {
        this.put(list, (c1, c2) -> LorcanaCardDTO.CHANGES_COMPARATOR.compare(c1.getCard(), c2.getCard()));
    }
}
