package com.pcagrade.retriever.card.tag.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.tag.CardTagDTO;
import com.pcagrade.retriever.card.tag.CardTagService;
import com.pcagrade.retriever.card.tag.type.CardTagType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cards/tags")
public class CardTagRestController {

    @Autowired
    private CardTagService cardTagService;

    @GetMapping
    public List<CardTagDTO> findAll() {
        return cardTagService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<CardTagDTO> findById(Ulid id) {
        return cardTagService.findById(id);
    }

    @PutMapping
    public void saveTag(@RequestBody CardTagDTO tag) {
        cardTagService.save(tag);
    }

    @PostMapping
    public Ulid addTag(@RequestBody CardTagDTO tag) {
        return cardTagService.save(tag);
    }

    @GetMapping("/types")
    public List<String> findAllTypes() {
        return Arrays.stream(CardTagType.values())
                .map(CardTagType::getCode)
                .toList();
    }
}
