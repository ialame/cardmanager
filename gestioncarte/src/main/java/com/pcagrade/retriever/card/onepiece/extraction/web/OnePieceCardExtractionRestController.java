package com.pcagrade.retriever.card.onepiece.extraction.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.onepiece.extraction.ExtractedOnePieceCardDTO;
import com.pcagrade.retriever.card.onepiece.extraction.OnePieceCardExtractionService;
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
@RequestMapping("/api/cards/onepiece")
public class OnePieceCardExtractionRestController {

    @Autowired
    private OnePieceCardExtractionService onePieceCardExtractionService;

    @PostMapping("/set/{setId}/extract")
    public void startExtractOnePieceCards(@PathVariable Ulid setId) {
        onePieceCardExtractionService.startExtraction(setId);
    }

    @GetMapping("/extracted")
    public synchronized List<ExtractedOnePieceCardDTO> getExtractedOnePieceCards() {
        return onePieceCardExtractionService.get();
    }

    @PutMapping("/extracted")
    public synchronized void getExtractedOnePieceCards(@RequestBody ExtractedOnePieceCardDTO card) {
        onePieceCardExtractionService.save(card);
    }

    @DeleteMapping("/extracted")
    public synchronized void clearExtractedOnePieceCards() {
        onePieceCardExtractionService.clear();
    }

    @DeleteMapping("/extracted/{id}")
    public synchronized ExtractedOnePieceCardDTO removeExtractedCard(@PathVariable Ulid id) {
        return onePieceCardExtractionService.delete(id);
    }

}
