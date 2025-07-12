package com.pcagrade.retriever.card.pokemon.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.extraction.ExtractedPokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.extraction.PokemonCardExtractionService;
import com.pcagrade.retriever.card.pokemon.extraction.history.PokemonCardExtractionHistoryService;
import com.pcagrade.retriever.progress.IProgressTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/" + PokemonCardRestController.BASE_PATH)
public class PokemonCardExtractionRestController {

    @Autowired
    private PokemonCardExtractionService pokemonCardExtractionService;

    @Autowired
    private PokemonCardExtractionHistoryService pokemonCardExtractionHistoryService;

    @GetMapping("/set/{setId}/extract")
    public List<ExtractedPokemonCardDTO> extractPokemonCards(@PathVariable Ulid setId, @RequestParam(value = "distribution", required = false, defaultValue = "false") boolean distribution, @RequestParam(value = "filters", required = false) List<String> filters) {
        return pokemonCardExtractionService.extractPokemonCards(setId, distribution, filters);
    }

    @GetMapping("/set/{setId}/extract/count")
    public int countPokemonCardsToExtract(@PathVariable Ulid setId, @RequestParam(value = "distribution", required = false, defaultValue = "false") boolean distribution, @RequestParam(value = "filters", required = false) List<String> filters) {
        return pokemonCardExtractionService.countPokemonCardsToExtract(setId, distribution, filters);
    }

    @PostMapping("/set/{setId}/extract")
    public void startExtractPokemonCards(@PathVariable Ulid setId, @RequestParam(value = "distribution", required = false, defaultValue = "false") boolean distribution, @RequestParam(value = "filters", required = false) List<String> filters) {
        pokemonCardExtractionService.startExtraction(setId, distribution, filters);
    }

    @GetMapping("/extract/progress")
    public Optional<IProgressTracker> getProgress() {
         return pokemonCardExtractionService.getProgress();
    }

    @GetMapping("/extracted")
    public synchronized List<ExtractedPokemonCardDTO> getExtractedPokemonCards() {
        return pokemonCardExtractionService.get();
    }

    @PostMapping("/extracted/bulk")
    public synchronized void addCards(@RequestBody List<ExtractedPokemonCardDTO> cards) {
        pokemonCardExtractionService.putCards(cards);
    }

    @PutMapping("/extracted/bulk")
    public synchronized void setCards(@RequestBody List<ExtractedPokemonCardDTO> cards) {
        pokemonCardExtractionService.clear();
        pokemonCardExtractionService.putCards(cards);
    }

    @GetMapping("/extracted/count")
    public synchronized int getExtractedPokemonCardsCount() {
        return pokemonCardExtractionService.get().size();
    }

    @PostMapping("/extracted")
    public synchronized void addCard(@RequestBody ExtractedPokemonCardDTO cards) {
        pokemonCardExtractionService.save(cards);
    }

    @PutMapping("/extracted")
    public synchronized void setCard(@RequestBody ExtractedPokemonCardDTO cards) {
        pokemonCardExtractionService.save(cards);
    }

    @DeleteMapping("/extracted/{id}")
    public synchronized ExtractedPokemonCardDTO removeExtractedCard(@PathVariable Ulid id) {
        return pokemonCardExtractionService.delete(id);
    }

    @DeleteMapping("/extracted")
    public synchronized void removeExtractedCards() {
        pokemonCardExtractionService.clear();
    }

    @PutMapping("/extracted/history")
    public void addExtractedHistory(@RequestBody PokemonCardDTO rawExtractedCard) {
        pokemonCardExtractionHistoryService.addHistory(rawExtractedCard);
    }

    @DeleteMapping("/set/{setId}/extracted/history")
    public void extractPokemonCards(@PathVariable Ulid setId) {
        pokemonCardExtractionHistoryService.clearExtractionHistory(setId);
    }

}
