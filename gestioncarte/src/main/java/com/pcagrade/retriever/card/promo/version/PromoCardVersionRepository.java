package com.pcagrade.retriever.card.promo.version;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRevisionRepository;
import com.pcagrade.retriever.card.TradingCardGame;

import java.util.List;

public interface PromoCardVersionRepository extends MasonRevisionRepository<PromoCardVersion, Ulid> {
    List<PromoCardVersion> findAllByTcg(TradingCardGame tcg);
}
