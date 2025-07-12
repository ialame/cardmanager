package com.pcagrade.retriever.card.pokemon.bracket.web;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.pokemon.bracket.BracketDTO;
import com.pcagrade.retriever.card.pokemon.bracket.BracketService;
import com.pcagrade.retriever.card.pokemon.web.PokemonCardRestController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/" + PokemonCardRestController.BASE_PATH + "/brackets")
public class BracketRestController {

	@Autowired
	private BracketService bracketService;

	@GetMapping
	public List<BracketDTO> getBrackets(@RequestParam(value = "name", required = false) String name) {
		if (StringUtils.isNotBlank(name)) {
			return bracketService.findAllByName(name);
		}
		return bracketService.getBrackets();
	}

	@GetMapping("/{id}")
	public Optional<BracketDTO> getBracket(@PathVariable("id") Ulid id) {
		return bracketService.findById(id);
	}

	@PutMapping
	public void saveBracket(@RequestBody BracketDTO bracket) {
		bracketService.saveBracket(bracket);
	}

	@PostMapping
	public Ulid addBracket(@RequestBody BracketDTO bracket) {
		return bracketService.saveBracket(bracket);
	}

	@DeleteMapping("/{id}")
	public void deleteBracket(@PathVariable("id") Ulid id) {
		bracketService.deleteBracket(id);
	}

}
