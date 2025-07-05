package com.pcagrade.retriever.card.lorcana.source.mushu.set;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MushuSetRepository extends MasonRepository<MushuSet, Integer> {

    List<MushuSet> findAllBySetId(Ulid setId);
}
