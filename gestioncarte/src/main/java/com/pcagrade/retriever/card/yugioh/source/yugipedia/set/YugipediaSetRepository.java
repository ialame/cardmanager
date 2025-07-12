package com.pcagrade.retriever.card.yugioh.source.yugipedia.set;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YugipediaSetRepository extends MasonRepository<YugipediaSet, Integer> {

    List<YugipediaSet> findAllBySetId(Ulid setId);
}
