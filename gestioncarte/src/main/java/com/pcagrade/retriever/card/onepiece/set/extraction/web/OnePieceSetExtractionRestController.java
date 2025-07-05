package com.pcagrade.retriever.card.onepiece.set.extraction.web;

import com.pcagrade.retriever.card.onepiece.set.OnePieceSetDTO;
import com.pcagrade.retriever.card.onepiece.set.extraction.OnePieceSetExtractionService;
import com.pcagrade.retriever.card.onepiece.set.web.OnePieceSetRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(OnePieceSetRestController.BASE_PATH)
public class OnePieceSetExtractionRestController {

    @Autowired
    private OnePieceSetExtractionService onePieceSetExtractionService;

    @GetMapping("/unsaved")
    public List<OnePieceSetDTO> unsavedSets() {
        return onePieceSetExtractionService.unsavedSets();
    }

    @GetMapping("/unsaved/{code}")
    public Optional<OnePieceSetDTO> unsavedSet(@PathVariable String code) {
        return onePieceSetExtractionService.findBySetCode(code);
    }

}
