package com.pcagrade.retriever.card.pokemon.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.revision.HistoryTreeDTO;
import com.pcagrade.retriever.card.pokemon.IPokemonCardService;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/" + PokemonCardRestController.BASE_PATH)
public class PokemonCardRestController {

    public static final String BASE_PATH = "cards/pokemon";
    @Autowired
	private IPokemonCardService pokemonCardService;

	@GetMapping("/{id}")
	public Optional<PokemonCardDTO> getCard(@PathVariable Ulid id) {
		return pokemonCardService.getCardById(id);
	}

	@GetMapping("/{id}/children")
	public List<PokemonCardDTO> getCardChildren(@PathVariable Ulid id) {
		return pokemonCardService.getCardChildrenById(id);
	}

	@GetMapping("/{id}/history")
	public HistoryTreeDTO<PokemonCardDTO> getCardHistory(@PathVariable Ulid id) {
		return pokemonCardService.getCardHistoryById(id);
	}

	@PutMapping
	public void saveCard(@RequestBody PokemonCardDTO card) {
		pokemonCardService.save(card);
	}

	@PostMapping
	public Ulid saveNewCard(@RequestBody PokemonCardDTO card) {
		return pokemonCardService.save(card);
	}

	@PutMapping("/save-all")
	public void saveCards(@RequestBody List<PokemonCardDTO> cards) {
		cards.forEach(pokemonCardService::save);
	}

	@DeleteMapping("/{id}")
	public void deleteCard(@PathVariable Ulid id) {
		pokemonCardService.deleteCard(id);
	}

	@DeleteMapping("/sets/{setId}/clear")
	public void clearSet(@PathVariable Ulid setId) {
		pokemonCardService.deleteInSet(setId);
	}

	@PostMapping("/search")
	public List<PokemonCardDTO> findSavedCards(@RequestBody PokemonCardDTO card) {
		return pokemonCardService.findSavedCards(card);
	}

	@PostMapping("/search/ids")
	public List<Ulid> findSavedCardIds(@RequestBody PokemonCardDTO card) {
		return findSavedCards(card).stream()
				.map(PokemonCardDTO::getId)
				.toList();
	}

	@PostMapping("/parent/search")
	public List<PokemonCardDTO> findParentCards(@RequestBody PokemonCardDTO card) {
		return pokemonCardService.findParentCard(card);
	}

	@PostMapping("/parent/search/ids")
	public List<Ulid> findParentCardIds(@RequestBody PokemonCardDTO card) {
		return findParentCards(card).stream()
				.map(PokemonCardDTO::getId)
				.toList();
	}

	@PutMapping("/{ids}/merge")
	public void mergeCards(@RequestBody PokemonCardDTO card, @PathVariable List<Ulid> ids) {
		pokemonCardService.merge(card, ids);
	}

	@GetMapping("/types")
	public List<String> getTypes() {
		return pokemonCardService.getAllTypes();
	}

	@GetMapping("/sets/{setId}/cards")
	public List<PokemonCardDTO> getAllCardsInSet(@PathVariable Ulid setId) {
		return pokemonCardService.getAllCardsInSet(setId);
	}

	@GetMapping("/brackets/{bracketId}/cards")
	public List<PokemonCardDTO> findCardsWithBracket(@PathVariable Ulid bracketId) {
		return pokemonCardService.findCardsWithBracket(bracketId);
	}

	@GetMapping("/promos/{promoId}/card")
	public Optional<PokemonCardDTO> findCardWithPromo(@PathVariable Ulid promoId) {
		return pokemonCardService.findCardWithPromo(promoId);
	}


}
