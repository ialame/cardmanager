package com.pcagrade.retriever.card;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends MasonRevisionRepository<Card, Ulid> {
}
