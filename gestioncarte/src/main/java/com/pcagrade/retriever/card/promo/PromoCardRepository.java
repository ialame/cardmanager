package com.pcagrade.retriever.card.promo;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRevisionRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface PromoCardRepository extends MasonRevisionRepository<PromoCard, Ulid> {
    List<PromoCard> findAllByCardId(Ulid cardId);
    List<PromoCard> findAllByEventId(Ulid eventId);

    List<PromoCard> findAllByEventIdIsNull();

}
