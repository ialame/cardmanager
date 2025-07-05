package com.pcagrade.retriever.card.onepiece.serie;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.onepiece.serie.translation.OnePieceSerieTranslationMapper;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = PCAMapperConfig.class,
        collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
        uses = {OnePieceSerieTranslationMapper.class})
public interface OnePieceSerieMapper {
    OnePieceSerieDTO mapToDTO(OnePieceSerie onePieceSerie);

    OnePieceSerie maFromDTO(OnePieceSerieDTO onePieceSerieDTO);

    void update(@MappingTarget OnePieceSerie serie, OnePieceSerieDTO dto);
}
