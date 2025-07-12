package com.pcagrade.retriever.card.onepiece.source.official.id;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OnePieceOfficialSiteIdRepository extends MasonRepository<OnePieceOfficialSiteId, Integer> {

    List<OnePieceOfficialSiteId> findAllBySetId(Ulid setId);

}
