package com.pcagrade.retriever.card.onepiece.source.official.id;

import com.pcagrade.retriever.PCAMapperConfig;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = PCAMapperConfig.class)
public interface OnePieceOfficialSiteIdMapper {

    @Mapping(target = "id", source = "officialSiteId")
    OnePieceOfficialSiteIdDTO mapToDTO(OnePieceOfficialSiteId id);

    @InheritInverseConfiguration
    OnePieceOfficialSiteId mapFromDTO(OnePieceOfficialSiteIdDTO dto);

    @InheritInverseConfiguration
    void update(@MappingTarget OnePieceOfficialSiteId id, OnePieceOfficialSiteIdDTO dto);
}
