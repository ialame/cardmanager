package com.pcagrade.retriever.card.onepiece.serie;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OnePieceSerieRepository extends MasonRepository<OnePieceSerie, Ulid> {

    @Query("from OnePieceSerie s join fetch s.translations t where lower(t.name) = lower(?1)")
    Optional<OnePieceSerie> findByName(String name);

}
