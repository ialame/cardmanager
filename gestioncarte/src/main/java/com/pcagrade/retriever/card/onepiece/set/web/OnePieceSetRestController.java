package com.pcagrade.retriever.card.onepiece.set.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.onepiece.set.OnePieceSetDTO;
import com.pcagrade.retriever.card.onepiece.set.OnePieceSetService;
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
@RequestMapping(OnePieceSetRestController.BASE_PATH)
public class OnePieceSetRestController {

    public static final String BASE_PATH = "/api/cards/onepiece/sets";

    @Autowired
    private OnePieceSetService onePieceSetService;

    @GetMapping
    public List<OnePieceSetDTO> getSets() {
        return onePieceSetService.getSets();
    }

    @GetMapping("/{id}")
    public Optional<OnePieceSetDTO> getById(@PathVariable Ulid id) {
        return onePieceSetService.findById(id);
    }

    @PutMapping
    public void saveSet(@RequestBody OnePieceSetDTO set) {
        createSet(set);
    }

    @PostMapping
    public Ulid createSet(@RequestBody OnePieceSetDTO set) {
        return onePieceSetService.save(set);
    }
}
