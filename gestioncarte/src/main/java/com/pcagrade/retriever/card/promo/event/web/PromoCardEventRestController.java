package com.pcagrade.retriever.card.promo.event.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.promo.event.PromoCardEventDTO;
import com.pcagrade.retriever.card.promo.event.PromoCardEventService;
import com.pcagrade.retriever.card.promo.web.PromoCardRestController;
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
@RequestMapping("/api/" + PromoCardEventRestController.BASE_PATH)
public class PromoCardEventRestController {

    public static final String BASE_PATH = PromoCardRestController.BASE_PATH + "/events";

    @Autowired
    private PromoCardEventService promoCardEventService;

    @GetMapping("/{id}")
    public Optional<PromoCardEventDTO> getEvent(@PathVariable Ulid id) {
        return promoCardEventService.findById(id);
    }

    @GetMapping
    public List<PromoCardEventDTO> getEvents() {
        return promoCardEventService.findAll();
    }

    @PutMapping
    public void saveEvent(@RequestBody PromoCardEventDTO event) {
        promoCardEventService.save(event);
    }

    @PostMapping
    public Ulid saveNewEvent(@RequestBody PromoCardEventDTO event) {
        return promoCardEventService.save(event);
    }

}
