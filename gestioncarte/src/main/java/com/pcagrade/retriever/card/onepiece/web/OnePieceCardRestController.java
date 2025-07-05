package com.pcagrade.retriever.card.onepiece.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.onepiece.OnePieceCardDTO;
import com.pcagrade.retriever.card.onepiece.OnePieceCardService;
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
@RequestMapping("/api/cards/onepiece")
public class OnePieceCardRestController {

    @Autowired
    private OnePieceCardService onePieceCardService;

    @GetMapping("/{id}")
    public Optional<OnePieceCardDTO> getById(@PathVariable Ulid id) {
        return onePieceCardService.findById(id);
    }

    @GetMapping("/sets/{setId}/cards")
    public List<OnePieceCardDTO> getInSet(@PathVariable Ulid setId) {
        return onePieceCardService.findAllInSet(setId);
    }

    @PostMapping("/sets/{id}/rebuild-ids-prim")
    public void rebuildIdsPrim(@PathVariable Ulid id) {
        onePieceCardService.rebuildIdsPrim(id);
    }

    @PutMapping
    public void saveCard(@RequestBody OnePieceCardDTO card) {
        createCard(card);
    }

    @PostMapping
    public Ulid createCard(@RequestBody OnePieceCardDTO card) {
        return onePieceCardService.save(card);
    }

    @PutMapping("/{ids}/merge")
    public void mergeCards(@RequestBody OnePieceCardDTO card, @PathVariable List<Ulid> ids) {
        onePieceCardService.merge(card, ids);
    }

    @GetMapping("/promos/{promoId}/card")
    public Optional<OnePieceCardDTO> findCardWithPromo(@PathVariable Ulid promoId) {
        return onePieceCardService.findCardWithPromo(promoId);
    }
}
