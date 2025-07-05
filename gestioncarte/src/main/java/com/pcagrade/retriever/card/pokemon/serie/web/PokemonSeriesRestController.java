package com.pcagrade.retriever.card.pokemon.serie.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.pokemon.serie.PokemonSerieDTO;
import com.pcagrade.retriever.card.pokemon.serie.PokemonSerieService;
import com.pcagrade.retriever.card.pokemon.web.PokemonCardRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/" + PokemonSeriesRestController.BASE_PATH)
public class PokemonSeriesRestController {

	public static final String BASE_PATH = PokemonCardRestController.BASE_PATH + "/series";

	@Autowired
	PokemonSerieService pokemonSerieService;

	@GetMapping
	public List<PokemonSerieDTO> getSeries() {
		return pokemonSerieService.getSeries();
	}

	@GetMapping("/unnumbered")
	public Optional<PokemonSerieDTO> getUnnumberedSerie() {
		return pokemonSerieService.getUnnumberedSerie();
	}

	@PutMapping
	public void saveSerie(@RequestBody PokemonSerieDTO serie) {
		createSerie(serie);
	}

	@PostMapping
	public Ulid createSerie(@RequestBody PokemonSerieDTO serie) {
		return pokemonSerieService.save(serie);
	}
}
