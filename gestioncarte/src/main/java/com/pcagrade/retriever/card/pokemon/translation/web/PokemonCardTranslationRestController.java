package com.pcagrade.retriever.card.pokemon.translation.web;

import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationService;
import com.pcagrade.retriever.card.pokemon.translation.SourcedPokemonCardTranslationDTO;
import com.pcagrade.retriever.card.pokemon.web.PokemonCardRestController;
import com.pcagrade.mason.localization.Localization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/" + PokemonCardTranslationRestController.BASE_PATH)
public class PokemonCardTranslationRestController {

    public static final String BASE_PATH = PokemonCardRestController.BASE_PATH + "/translations";

    @Autowired
    private PokemonCardTranslationService pokemonCardTranslationService;

    @PostMapping
    public List<SourcedPokemonCardTranslationDTO> getTranslations(@RequestBody PokemonCardDTO card, @RequestParam(defaultValue = "us") String from) {
        return pokemonCardTranslationService.getTranslations(card, Localization.getByCode(from));
    }

    @PostMapping("/{to}")
    public List<SourcedPokemonCardTranslationDTO> getTranslations(@RequestBody PokemonCardDTO card, @PathVariable String to, @RequestParam(defaultValue = "us") String from) {
        var toLocalization = Localization.getByCode(to);

        return getTranslations(card, from).stream()
                .filter(t -> t.localization() == toLocalization)
                .toList();
    }
}
