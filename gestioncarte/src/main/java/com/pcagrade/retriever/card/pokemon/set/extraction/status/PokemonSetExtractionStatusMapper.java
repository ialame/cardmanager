package com.pcagrade.retriever.card.pokemon.set.extraction.status;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.set.extraction.status.CardSetExtractionStatus;
import com.pcagrade.retriever.localization.LocalizationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = PCAMapperConfig.class, uses = LocalizationMapper.class)
public interface PokemonSetExtractionStatusMapper {

    PokemonSetExtractionStatusDTO mapToDto(CardSetExtractionStatus status);
    CardSetExtractionStatus mapFromDto(PokemonSetExtractionStatusDTO dto);
    void update(@MappingTarget CardSetExtractionStatus status, PokemonSetExtractionStatusDTO dto);

}
