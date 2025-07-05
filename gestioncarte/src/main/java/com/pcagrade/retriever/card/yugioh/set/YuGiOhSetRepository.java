package com.pcagrade.retriever.card.yugioh.set;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YuGiOhSetRepository extends MasonRepository<YuGiOhSet, Ulid> {

    boolean existsByIdPca(int idPca);

}
