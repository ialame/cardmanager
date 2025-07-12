package com.pcagrade.retriever.card.promo.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.promo.PromoCardDTO;
import com.pcagrade.retriever.card.promo.PromoCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/" + PromoCardRestController.BASE_PATH)
public class PromoCardRestController {

    public static final String BASE_PATH = "cards/promos";

    @Autowired
    private PromoCardService promoCardService;

    @GetMapping("/{id}")
    public Optional<PromoCardDTO> getPromo(@PathVariable Ulid id) {
        return promoCardService.findById(id);
    }

    @PutMapping
    public void savePromo(@RequestBody PromoCardDTO promo) {
        promoCardService.save(promo);
    }

    @PostMapping
    public Ulid saveNewPromo(@RequestBody PromoCardDTO promo) {
        return promoCardService.save(promo);
    }
}
