package com.pcagrade.retriever.card.promo.event.extraction.handler.onepiece;

import com.pcagrade.retriever.card.TradingCardGame;
import com.pcagrade.retriever.card.promo.event.extraction.ExtractedPromoCardEventDTO;
import com.pcagrade.retriever.card.promo.event.extraction.handler.IPromoEventHandler;

import jakarta.annotation.Nonnull;

public interface IOnePiecePromoEventHandler extends IPromoEventHandler {

    @Override
    default boolean supports(@Nonnull ExtractedPromoCardEventDTO dto) {
        return dto.getTcg() == TradingCardGame.ONE_PIECE;
    }
}
