package com.pcagrade.retriever.card.extraction.status;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.localization.LocalizationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = PCAMapperConfig.class, uses = LocalizationMapper.class)
public interface CardExtractionStatusMapper {

    CardExtractionStatusDTO mapToDto(CardExtractionStatus status);
    CardExtractionStatus mapFromDto(CardExtractionStatusDTO dto);
    void update(@MappingTarget CardExtractionStatus status, CardExtractionStatusDTO dto);

}
