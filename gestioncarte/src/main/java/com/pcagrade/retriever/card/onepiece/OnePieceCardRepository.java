package com.pcagrade.retriever.card.onepiece;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRevisionRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OnePieceCardRepository extends MasonRevisionRepository<OnePieceCard, Ulid> {

     @Query("select c from OnePieceCard c join fetch c.cardSets cs where cs.id = :setId")
     List<OnePieceCard> findInSet(Ulid setId);

     @Query("select c from OnePieceCard c join fetch c.cardSets cs where cs.id = :setId and c.number = :number")
     List<OnePieceCard> findAllBySetIdAndNumber(Ulid setId, String number);

    @Query("select c from OnePieceCard c join fetch c.promoCards pc where pc.id = :promoId")
    Optional<OnePieceCard> findByPromoId(Ulid promoId);
}
