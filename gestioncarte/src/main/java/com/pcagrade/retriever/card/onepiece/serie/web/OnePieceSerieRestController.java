package com.pcagrade.retriever.card.onepiece.serie.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.onepiece.serie.OnePieceSerieDTO;
import com.pcagrade.retriever.card.onepiece.serie.OnePieceSerieService;
import com.pcagrade.retriever.card.onepiece.source.official.OnePieceOfficialSiteService;
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
@RequestMapping("/api/cards/onepiece/series")
public class OnePieceSerieRestController {

    @Autowired
    private OnePieceSerieService onePieceSerieService;
    @Autowired
    private OnePieceOfficialSiteService onePieceOfficialSiteService;

    @GetMapping
    public List<OnePieceSerieDTO> getSeries() {
        return onePieceSerieService.getSeries();
    }

    @GetMapping("/{id}")
    public Optional<OnePieceSerieDTO> getById(@PathVariable Ulid id) {
        return onePieceSerieService.findById(id);
    }

    @PutMapping
    public void saveSerie(@RequestBody OnePieceSerieDTO serie) {
        createSerie(serie);
    }

    @PostMapping
    public Ulid createSerie(@RequestBody OnePieceSerieDTO serie) {
        return onePieceSerieService.save(serie);
    }

    @GetMapping("/unsaved/{code}")
    public Optional<OnePieceSerieDTO> unsavedSerie(@PathVariable String code) {
        return onePieceOfficialSiteService.findSerieByCode(code);
    }

}
