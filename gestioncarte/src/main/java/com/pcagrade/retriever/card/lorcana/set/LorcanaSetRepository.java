package com.pcagrade.retriever.card.lorcana.set;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LorcanaSetRepository extends MasonRepository<LorcanaSet, Ulid> {
}
