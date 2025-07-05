package com.pcagrade.retriever.card.promo.event.trait;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRevisionRepository;
import com.pcagrade.retriever.card.TradingCardGame;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface PromoCardEventTraitRepository extends MasonRevisionRepository<PromoCardEventTrait, Ulid> {
    List<PromoCardEventTrait> findAllByTcg(TradingCardGame tcg);
}
