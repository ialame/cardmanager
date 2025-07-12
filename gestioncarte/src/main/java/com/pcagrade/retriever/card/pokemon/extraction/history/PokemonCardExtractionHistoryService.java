package com.pcagrade.retriever.card.pokemon.extraction.history;

import com.pcagrade.retriever.card.extraction.history.AbstractCardExtractionHistoryService;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetRepository;
import com.pcagrade.retriever.card.set.CardSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PokemonCardExtractionHistoryService extends AbstractCardExtractionHistoryService<PokemonCardDTO> {

    @Autowired
    private PokemonSetRepository pokemonSetRepository;

    public PokemonCardExtractionHistoryService() {
        super(PokemonCardDTO.class);
    }

    @Override
    protected Set<CardSet> getSet(PokemonCardDTO card) {
        return card.getSetIds().stream()
                .filter(Objects::nonNull)
                .<CardSet>mapMulti((id, downstream) -> pokemonSetRepository.findById(id).ifPresent(downstream))
                .collect(Collectors.toSet());
    }
}
