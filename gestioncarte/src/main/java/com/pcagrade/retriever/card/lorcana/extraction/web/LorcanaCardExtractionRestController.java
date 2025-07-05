package com.pcagrade.retriever.card.lorcana.extraction.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.lorcana.extraction.ExtractedLorcanaCardDTO;
import com.pcagrade.retriever.card.lorcana.extraction.LorcanaCardExtractionService;
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

@RestController
@RequestMapping("/api/cards/lorcana")
public class LorcanaCardExtractionRestController {

    @Autowired
    private LorcanaCardExtractionService lorcanaCardExtractionService;

    @PostMapping("/set/{setId}/extract")
    public void startExtractLorcanaeCards(@PathVariable Ulid setId) {
        lorcanaCardExtractionService.startExtraction(setId);
    }

    @GetMapping("/extracted")
    public synchronized List<ExtractedLorcanaCardDTO> getExtractedLorcanaCards() {
        return lorcanaCardExtractionService.get();
    }

    @PutMapping("/extracted")
    public synchronized void getExtractedLorcanaCards(@RequestBody ExtractedLorcanaCardDTO card) {
        lorcanaCardExtractionService.save(card);
    }

    @DeleteMapping("/extracted")
    public synchronized void clearExtractedLorcanaCards() {
        lorcanaCardExtractionService.clear();
    }

    @DeleteMapping("/extracted/{id}")
    public synchronized ExtractedLorcanaCardDTO removeExtractedCard(@PathVariable Ulid id) {
        return lorcanaCardExtractionService.delete(id);
    }

}
