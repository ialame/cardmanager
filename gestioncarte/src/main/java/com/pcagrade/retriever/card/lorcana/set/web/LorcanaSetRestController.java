package com.pcagrade.retriever.card.lorcana.set.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.lorcana.set.LorcanaSetDTO;
import com.pcagrade.retriever.card.lorcana.set.LorcanaSetService;
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
@RequestMapping(LorcanaSetRestController.BASE_PATH)
public class LorcanaSetRestController {

    public static final String BASE_PATH = "/api/cards/lorcana/sets";

    @Autowired
    private LorcanaSetService lorcanaSetService;

    @GetMapping
    public List<LorcanaSetDTO> getSets() {
        return lorcanaSetService.getSets();
    }

    @GetMapping("/{id}")
    public Optional<LorcanaSetDTO> getById(@PathVariable Ulid id) {
        return lorcanaSetService.findById(id);
    }

    @PutMapping
    public void saveSet(@RequestBody LorcanaSetDTO set) {
        createSet(set);
    }

    @PostMapping
    public Ulid createSet(@RequestBody LorcanaSetDTO set) {
        return lorcanaSetService.save(set);
    }
}
