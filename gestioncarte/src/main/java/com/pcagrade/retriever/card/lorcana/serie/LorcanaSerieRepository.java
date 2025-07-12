package com.pcagrade.retriever.card.lorcana.serie;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LorcanaSerieRepository extends MasonRepository<LorcanaSerie, Ulid> {

}
