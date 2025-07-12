package com.pcagrade.retriever.card.onepiece.extraction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.onepiece.OnePieceCardDTO;
import com.pcagrade.retriever.card.onepiece.OnePieceCardService;
import com.pcagrade.retriever.card.onepiece.source.official.OnePieceOfficialSiteService;
import com.pcagrade.retriever.extraction.AbstractExtractionService;
import com.pcagrade.retriever.progress.ConsolidatedProgressTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class OnePieceCardExtractionService extends AbstractExtractionService<ExtractedOnePieceCardDTO> {

    public static final String NAME = "onepiece_cards";

    public static final String TRACKER_MESSAGE = "Extraction des cartes One Piece";

    @Autowired
    private OnePieceOfficialSiteService onePieceOfficialSiteService;
    @Autowired
    private OnePieceCardService onePieceCardService;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ConsolidatedProgressTracker progressTracker = new ConsolidatedProgressTracker(TRACKER_MESSAGE);

    public OnePieceCardExtractionService(ObjectMapper mapper) {
        super(NAME, mapper, ExtractedOnePieceCardDTO.class);
    }

    public void startExtraction(Ulid setId) {
        executorService.execute(() -> putCards(extractCards(setId)));
    }

    public List<ExtractedOnePieceCardDTO> extractCards(Ulid setId) {
        return onePieceOfficialSiteService.getCards(setId).stream()
                .map(c -> {
                    var dto = new ExtractedOnePieceCardDTO();

                    dto.setCard(c);
                    c.setSetIds(List.of(setId));
                    dto.setSavedCards(onePieceCardService.findSavedCard(c));
                    return dto;
                })
                .filter(dto -> dto.getSavedCards().isEmpty() || dto.getSavedCards().stream().noneMatch(c -> OnePieceCardDTO.CHANGES_COMPARATOR.compare(c, dto.getCard()) == 0))
                .toList();
    }

    public void putCards(List<ExtractedOnePieceCardDTO> list) {
        this.put(list, (c1, c2) -> OnePieceCardDTO.CHANGES_COMPARATOR.compare(c1.getCard(), c2.getCard()));
    }
}
