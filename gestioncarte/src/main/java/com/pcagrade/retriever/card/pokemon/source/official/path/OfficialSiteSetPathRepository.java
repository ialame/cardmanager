package com.pcagrade.retriever.card.pokemon.source.official.path;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfficialSiteSetPathRepository extends MasonRepository<OfficialSiteSetPath, Integer> {

	List<OfficialSiteSetPath> findAllBySetId(Ulid setId);
}
