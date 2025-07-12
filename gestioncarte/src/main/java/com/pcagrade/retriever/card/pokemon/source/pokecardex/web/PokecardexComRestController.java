package com.pcagrade.retriever.card.pokemon.source.pokecardex.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.pokemon.source.pokecardex.code.PokecardexSetCodeService;
import com.pcagrade.retriever.card.pokemon.web.PokemonCardRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/" + PokecardexComRestController.BASE_PATH)
public class PokecardexComRestController {

    public static final String BASE_PATH = PokemonCardRestController.BASE_PATH + "/pokecardex";

    @Autowired
    private PokecardexSetCodeService pokecardexSetCodeService;

    @GetMapping("/{code}/set-id")
    public Optional<Ulid> findByCode(@PathVariable String code) {
        return pokecardexSetCodeService.findByCode(code);
    }

}
