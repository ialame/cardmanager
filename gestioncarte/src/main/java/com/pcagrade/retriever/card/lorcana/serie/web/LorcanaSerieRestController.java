package com.pcagrade.retriever.card.lorcana.serie.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.lorcana.serie.LorcanaSerieDTO;
import com.pcagrade.retriever.card.lorcana.serie.LorcanaSerieService;
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
@RequestMapping("/api/cards/lorcana/series")
public class LorcanaSerieRestController {

    @Autowired
    private LorcanaSerieService lorcanaSerieService;
    @GetMapping
    public List<LorcanaSerieDTO> getSeries() {
        return lorcanaSerieService.getSeries();
    }

    @GetMapping("/{id}")
    public Optional<LorcanaSerieDTO> getById(@PathVariable Ulid id) {
        return lorcanaSerieService.findById(id);
    }

    @PutMapping
    public void saveSerie(@RequestBody LorcanaSerieDTO serie) {
        createSerie(serie);
    }

    @PostMapping
    public Ulid createSerie(@RequestBody LorcanaSerieDTO serie) {
        return lorcanaSerieService.save(serie);
    }
}
