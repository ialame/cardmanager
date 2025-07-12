package com.pcagrade.retriever.card.lorcana;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LorcanaCardRepository extends MasonRevisionRepository<LorcanaCard, Ulid> {
}
