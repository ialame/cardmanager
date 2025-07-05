package com.pcagrade.retriever.card.yugioh.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.yugioh.YuGiOhCardDTO;
import com.pcagrade.retriever.card.yugioh.YuGiOhCardService;
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
@RequestMapping("/api/" + YuGiOhCardRestController.BASE_PATH)
public class YuGiOhCardRestController {

    public static final String BASE_PATH = "cards/yugioh";

    @Autowired
    private YuGiOhCardService yugiohCardService;

    @GetMapping("/{id}")
    public Optional<YuGiOhCardDTO> getById(@PathVariable Ulid id) {
        return yugiohCardService.findById(id);
    }

    @PutMapping
    public void saveCard(@RequestBody YuGiOhCardDTO card) {
        createCard(card);
    }

    @PostMapping
    public Ulid createCard(@RequestBody YuGiOhCardDTO card) {
        return yugiohCardService.save(card);
    }

    @GetMapping("/sets/{setId}/cards")
    public List<YuGiOhCardDTO> getAllCardsInSet(@PathVariable Ulid setId) {
        return yugiohCardService.findAllCardsInSet(setId);
    }

    @PostMapping("/sets/{id}/rebuild-ids-prim")
    public void rebuildIdsPrim(@PathVariable Ulid id) {
        yugiohCardService.rebuildIdsPrim(id);
    }

    @PutMapping("/{ids}/merge")
    public void mergeCards(@RequestBody YuGiOhCardDTO card, @PathVariable List<Ulid> ids) {
        yugiohCardService.merge(card, ids);
    }

}
