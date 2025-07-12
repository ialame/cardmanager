package com.pcagrade.retriever.card.onepiece.set;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OnePieceSetRepository extends MasonRepository<OnePieceSet, Ulid> {

    boolean existsByIdPca(int idPca);
}
