package com.pcagrade.retriever.card.yugioh;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRevisionRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YuGiOhCardRepository extends MasonRevisionRepository<YuGiOhCard, Ulid> {

    @Query("select c from YuGiOhCard c join fetch c.cardSets cs where cs.id = :setId")
    List<YuGiOhCard> findInSet(Ulid setId);
}
