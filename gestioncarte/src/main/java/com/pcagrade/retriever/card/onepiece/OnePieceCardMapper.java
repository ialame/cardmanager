package com.pcagrade.retriever.card.onepiece;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.extraction.status.CardExtractionStatusMapper;
import com.pcagrade.retriever.card.onepiece.translation.OnePieceCardTranslationMapper;
import com.pcagrade.retriever.card.promo.PromoCardMapper;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = PCAMapperConfig.class,
        collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
        uses = {OnePieceCardTranslationMapper.class, PromoCardMapper.class, CardExtractionStatusMapper.class})
public interface OnePieceCardMapper {

    @Mapping(target = "artist", source = "artist.name")
    @Mapping(target = "promos", source = "promoCards")
    OnePieceCardDTO mapToDTO(OnePieceCard card);

    @AfterMapping
    default void afterMapping(@MappingTarget OnePieceCardDTO dto, OnePieceCard card) {
        dto.setSetIds(card.getCardSets().stream()
                .map(AbstractUlidEntity::getId)
                .toList());

        PromoCardMapper.applyPromoUsed(dto.getPromos(), card.getPromoUsed());
    }

    @Mapping(target = "artist", ignore = true)
    void update(@MappingTarget OnePieceCard card, OnePieceCardDTO dto);
}
