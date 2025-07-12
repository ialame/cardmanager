package com.pcagrade.retriever.card.lorcana.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.lorcana.LorcanaCardDTO;
import com.pcagrade.retriever.card.lorcana.LorcanaCardService;
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
@RequestMapping("/api/cards/lorcana")
public class LorcanaCardRestController {

    @Autowired
    private LorcanaCardService lorcanaCardService;

    @GetMapping("/{id}")
    public Optional<LorcanaCardDTO> getById(@PathVariable Ulid id) {
        return lorcanaCardService.findById(id);
    }

    @GetMapping("/sets/{setId}/cards")
    public List<LorcanaCardDTO> getInSet(@PathVariable Ulid setId) {
        return lorcanaCardService.findAllInSet(setId);
    }

    @PutMapping
    public void saveCard(@RequestBody LorcanaCardDTO card) {
        createCard(card);
    }

    @PostMapping
    public Ulid createCard(@RequestBody LorcanaCardDTO card) {
        return lorcanaCardService.save(card);
    }

    @PutMapping("/{ids}/merge")
    public void mergeCards(@RequestBody LorcanaCardDTO card, @PathVariable List<Ulid> ids) {
        lorcanaCardService.merge(card, ids);
    }

    @PostMapping("/sets/{id}/rebuild-ids-prim")
    public void rebuildIdsPrim(@PathVariable Ulid id) {
        lorcanaCardService.rebuildIdsPrim(id);
    }
}
