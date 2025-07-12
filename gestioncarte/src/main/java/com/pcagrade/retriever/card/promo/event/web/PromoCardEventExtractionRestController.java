package com.pcagrade.retriever.card.promo.event.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.TradingCardGame;
import com.pcagrade.retriever.card.promo.event.extraction.ExtractedPromoCardEventDTO;
import com.pcagrade.retriever.card.promo.event.extraction.PromoCardEventExtractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/" + PromoCardEventRestController.BASE_PATH)
public class PromoCardEventExtractionRestController {


    @Autowired
    private PromoCardEventExtractionService promoCardEventExtractionService;

    @GetMapping("/extract")
    public List<ExtractedPromoCardEventDTO> extractPromoCardEvents(@RequestParam(value = "from", required = false) Ulid from) {
        return promoCardEventExtractionService.extractEvents(from != null ? List.of(from) : Collections.emptyList());
    }

    @GetMapping("/extracted")
    public List<ExtractedPromoCardEventDTO> getExtractedPromoCardEvents(@RequestParam(value = "tcg", required = false) TradingCardGame tcg) {
        return promoCardEventExtractionService.get(tcg);
    }

    @GetMapping("/extracted/{id}")
    public Optional<ExtractedPromoCardEventDTO> getExtractedPromoCardEvent(@PathVariable("id") Ulid id) {
        return promoCardEventExtractionService.get(id);
    }

    @DeleteMapping("/extracted/{id}")
    public void deleteExtractedPromoCardEvent(@PathVariable("id") Ulid id) {
        promoCardEventExtractionService.delete(id);
    }

    @PostMapping("/extracted/reload")
    public ExtractedPromoCardEventDTO regenerateExtractedEvent(@RequestBody ExtractedPromoCardEventDTO dto) {
        return promoCardEventExtractionService.regenerateExtractedEvent(dto);
    }

    @PostMapping("/extract")
    public void startExtractPromoCardEvents(@RequestBody(required = false) List<Ulid> filters) {
        promoCardEventExtractionService.clear();
        promoCardEventExtractionService.startExtraction(filters);
    }

}
