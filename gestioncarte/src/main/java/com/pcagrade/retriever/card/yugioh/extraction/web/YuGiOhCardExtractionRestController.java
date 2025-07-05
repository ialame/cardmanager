package com.pcagrade.retriever.card.yugioh.extraction.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.yugioh.extraction.ExtractedYuGiOhCardDTO;
import com.pcagrade.retriever.card.yugioh.extraction.YuGiOhCardExtractionService;
import com.pcagrade.retriever.card.yugioh.web.YuGiOhCardRestController;
import com.pcagrade.retriever.progress.IProgressTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/" + YuGiOhCardRestController.BASE_PATH)
public class YuGiOhCardExtractionRestController {


    @Autowired
    private YuGiOhCardExtractionService yuGiOhCardExtractionService;

    @PostMapping("/set/{setId}/extract")
    public void startExtractPokemonCards(@PathVariable Ulid setId) {
        yuGiOhCardExtractionService.startExtraction(setId);
    }

    @GetMapping("/extract/progress")
    public Optional<IProgressTracker> getProgress() {
        return yuGiOhCardExtractionService.getProgress();
    }

    @GetMapping("/extracted")
    public synchronized List<ExtractedYuGiOhCardDTO> getExtractedYuGiOhCards() {
        return yuGiOhCardExtractionService.get();
    }

    @DeleteMapping("/extracted")
    public synchronized void clearExtractedYuGiOhCards() {
        yuGiOhCardExtractionService.clear();
    }

    @DeleteMapping("/extracted/{id}")
    public synchronized ExtractedYuGiOhCardDTO removeExtractedCard(@PathVariable Ulid id) {
        return yuGiOhCardExtractionService.delete(id);
    }

    @PostMapping("/extracted/bulk")
    public synchronized void addCards(@RequestBody List<ExtractedYuGiOhCardDTO> cards) {
        yuGiOhCardExtractionService.putCards(cards);
    }

    @PutMapping("/extracted/bulk")
    public synchronized void setCards(@RequestBody List<ExtractedYuGiOhCardDTO> cards) {
        yuGiOhCardExtractionService.clear();
        yuGiOhCardExtractionService.putCards(cards);
    }
}
