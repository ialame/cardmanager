package com.pcagrade.retriever.card.pokemon.source.pokecardex.code;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PokecardexSetCodeRepository extends MasonRepository<PokecardexSetCode, Integer> {

    Optional<PokecardexSetCode> findBySetId(Ulid setId);
    Optional<PokecardexSetCode> findFirstByCode(String code);
}
