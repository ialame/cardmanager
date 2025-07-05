package com.pcagrade.retriever.card.yugioh.serie.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.yugioh.serie.YuGiOhSerieDTO;
import com.pcagrade.retriever.card.yugioh.serie.YuGiOhSerieService;
import com.pcagrade.retriever.card.yugioh.web.YuGiOhCardRestController;
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
@RequestMapping("/api/" + YuGiOhSerieRestController.BASE_PATH)
public class YuGiOhSerieRestController {

    public static final String BASE_PATH = YuGiOhCardRestController.BASE_PATH + "/series";

    @Autowired
    private YuGiOhSerieService yuGiOhSerieService;

    @GetMapping
    public List<YuGiOhSerieDTO> getAll() {
        return yuGiOhSerieService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<YuGiOhSerieDTO> getById(@PathVariable Ulid id) {
        return yuGiOhSerieService.findById(id);
    }

    @PutMapping
    public void saveSerie(@RequestBody YuGiOhSerieDTO serie) {
        createSerie(serie);
    }

    @PostMapping
    public Ulid createSerie(@RequestBody YuGiOhSerieDTO serie) {
        return yuGiOhSerieService.save(serie);
    }

}
