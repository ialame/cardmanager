package com.pcagrade.retriever.card.set.extraction.status;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CardSetExtractionStatusRepository extends MasonRepository<CardSetExtractionStatus, Integer> {

    @Query("delete from CardSetExtractionStatus s where treat(s.target as CardSet).id = ?1") // FIXME is it required
    @Modifying
    void deleteByTargetId(Ulid id);
}
