package com.pcagrade.retriever.card.yugioh.set.extraction.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.yugioh.set.extraction.ExtractedYuGiOhSetDTO;
import com.pcagrade.retriever.card.yugioh.set.extraction.YuGiOhSetExtractionService;
import com.pcagrade.retriever.card.yugioh.set.web.YuGiOhSetRestController;
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
@RequestMapping("/api/" + YuGiOhSetRestController.BASE_PATH)
public class YuGiOhSetExtractionRestController {


    @Autowired
    private YuGiOhSetExtractionService yuGiOhSetExtractionService;

    @PostMapping("/extract")
    public void startExtractPokemonSets() {
        yuGiOhSetExtractionService.extract();
    }

    @GetMapping("/extracted")
    public synchronized List<ExtractedYuGiOhSetDTO> getExtractedYuGiOhSets() {
        return yuGiOhSetExtractionService.get();
    }

    @GetMapping("/extracted/{id}")
    public synchronized Optional<ExtractedYuGiOhSetDTO> getExtractedYuGiOhSets(@PathVariable Ulid id) {
        return yuGiOhSetExtractionService.get(id);
    }

    @DeleteMapping("/extracted")
    public synchronized void clearExtractedYuGiOhSets() {
        yuGiOhSetExtractionService.clear();
    }

    @DeleteMapping("/extracted/{id}")
    public synchronized ExtractedYuGiOhSetDTO removeExtractedSet(@PathVariable Ulid id) {
        return yuGiOhSetExtractionService.delete(id);
    }

    @PostMapping("/extracted/bulk")
    public synchronized void addSets(@RequestBody List<ExtractedYuGiOhSetDTO> sets) {
        yuGiOhSetExtractionService.putSets(sets);
    }

    @PutMapping("/extracted/bulk")
    public synchronized void setSets(@RequestBody List<ExtractedYuGiOhSetDTO> sets) {
        yuGiOhSetExtractionService.clear();
        yuGiOhSetExtractionService.putSets(sets);
    }
}
