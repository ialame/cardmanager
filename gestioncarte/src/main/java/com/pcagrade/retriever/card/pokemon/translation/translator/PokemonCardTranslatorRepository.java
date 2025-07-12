package com.pcagrade.retriever.card.pokemon.translation.translator;

import com.pcagrade.mason.jpa.repository.MasonRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface PokemonCardTranslatorRepository extends MasonRepository<PokemonCardTranslator, Integer> {

    Optional<PokemonCardTranslator> findByCode(String code);

}
