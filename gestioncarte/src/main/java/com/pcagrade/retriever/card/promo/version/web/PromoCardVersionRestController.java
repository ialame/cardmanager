package com.pcagrade.retriever.card.promo.version.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.promo.version.PromoCardVersionDTO;
import com.pcagrade.retriever.card.promo.version.PromoCardVersionService;
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
@RequestMapping("/api/" + PromoCardVersionRestController.BASE_PATH)
public class PromoCardVersionRestController {

    public static final String BASE_PATH = PromoCardRestController.BASE_PATH + "/versions";
    
    @Autowired
    private PromoCardVersionService promoCardVersionService;

    @GetMapping("/{id}")
    public Optional<PromoCardVersionDTO> getVersion(@PathVariable Ulid id) {
        return promoCardVersionService.findById(id);
    }

    @GetMapping
    public List<PromoCardVersionDTO> getVersions() {
        return promoCardVersionService.findAll();
    }

    @PutMapping
    public void saveVersion(@RequestBody PromoCardVersionDTO version) {
        promoCardVersionService.save(version);
    }

    @PostMapping
    public Ulid saveNewVersion(@RequestBody PromoCardVersionDTO version) {
        return promoCardVersionService.save(version);
    }
}
