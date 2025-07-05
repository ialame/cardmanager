package com.pcagrade.retriever.card.pokemon.translation.translator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = { "pokemonCardTranslator" })
public class PokemonCardTranslatorService {

    @Autowired
    private PokemonCardTranslatorRepository pokemonCardTranslatorRepository;

    @Autowired
    private PokemonCardTranslatorMapper pokemonCardTranslatorMapper;


    @Cacheable
    public List<PokemonCardTranslatorDTO> getTranslators() {
        return pokemonCardTranslatorRepository.findAll().stream()
                .map(pokemonCardTranslatorMapper::mapToDto)
                .toList();
    }

    @Cacheable("pokemonCardTranslatorWeight")
    public int getWeight(String code) {
        return pokemonCardTranslatorRepository.findByCode(code)
                .map(PokemonCardTranslator::getWeight)
                .orElse(0);
    }
}
