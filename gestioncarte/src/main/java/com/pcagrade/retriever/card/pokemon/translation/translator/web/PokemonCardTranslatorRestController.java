package com.pcagrade.retriever.card.pokemon.translation.translator.web;

import com.pcagrade.retriever.card.pokemon.translation.translator.PokemonCardTranslatorDTO;
import com.pcagrade.retriever.card.pokemon.translation.translator.PokemonCardTranslatorService;
import com.pcagrade.retriever.card.pokemon.web.PokemonCardRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/" + PokemonCardTranslatorRestController.BASE_PATH)
public class PokemonCardTranslatorRestController {

    public static final String BASE_PATH = PokemonCardRestController.BASE_PATH + "/translators";

    @Autowired
    private PokemonCardTranslatorService pokemonCardTranslatorService;

    @GetMapping
    public List<PokemonCardTranslatorDTO> getTranslators() {
        return pokemonCardTranslatorService.getTranslators();
    }

}
