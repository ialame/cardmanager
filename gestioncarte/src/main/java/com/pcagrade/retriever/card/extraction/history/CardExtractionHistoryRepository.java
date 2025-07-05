package com.pcagrade.retriever.card.extraction.history;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface CardExtractionHistoryRepository extends MasonRepository<CardExtractionHistory, Integer> {

    List<CardExtractionHistory> findAllByCardSetsId(Ulid cardSetsId);

    void deleteAllByCardSetsId(Ulid cardSetsId);

}
