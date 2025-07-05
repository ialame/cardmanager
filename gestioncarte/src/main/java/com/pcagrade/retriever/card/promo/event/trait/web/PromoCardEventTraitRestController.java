package com.pcagrade.retriever.card.promo.event.trait.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.revision.HistoryTreeDTO;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTraitDTO;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTraitService;
import com.pcagrade.retriever.card.promo.event.web.PromoCardEventRestController;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/" + PromoCardEventTraitRestController.BASE_PATH)
public class PromoCardEventTraitRestController {

    public static final String BASE_PATH = PromoCardEventRestController.BASE_PATH + "/traits";

    @Autowired
    private PromoCardEventTraitService promoCardEventTraitService;

    @GetMapping("/{id}")
    public Optional<PromoCardEventTraitDTO> getEventTrait(@PathVariable Ulid id) {
        return promoCardEventTraitService.findById(id);
    }

    @GetMapping("/{id}/history")
    public HistoryTreeDTO<PromoCardEventTraitDTO> getTraitHistory(@PathVariable Ulid id) {
        return promoCardEventTraitService.getHistoryById(id);
    }

    @GetMapping
    public List<PromoCardEventTraitDTO> getEventTraits() {
        return promoCardEventTraitService.findAll();
    }

    @PutMapping
    public void saveEventTrait(@RequestBody PromoCardEventTraitDTO trait) {
        promoCardEventTraitService.save(trait);
    }

    @PostMapping
    public Ulid saveNewEventTrait(@RequestBody PromoCardEventTraitDTO trait) {
        return promoCardEventTraitService.save(trait);
    }

    @PutMapping("/{ids}/merge")
    public void mergeEventTraits(@RequestBody PromoCardEventTraitDTO trait, @PathVariable List<Ulid> ids) {
        promoCardEventTraitService.merge(trait, ids);
    }

}
