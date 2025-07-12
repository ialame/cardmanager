package com.pcagrade.retriever.card.pokemon.source.ptcgo;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PtcgoSetRepository extends MasonRepository<PtcgoSet, Integer> {
	
	List<PtcgoSet> findAllBySetId(Ulid setId);
}
