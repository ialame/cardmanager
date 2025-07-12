package com.pcagrade.retriever.card.pokemon.source.pkmncards.path;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PkmncardsComSetPathRepository extends MasonRepository<PkmncardsComSetPath, Ulid> {

	Optional<PkmncardsComSetPath> findBySetId(Ulid setId);
}
