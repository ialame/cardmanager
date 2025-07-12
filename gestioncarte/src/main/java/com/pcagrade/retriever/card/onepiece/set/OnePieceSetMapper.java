package com.pcagrade.retriever.card.onepiece.set;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.onepiece.set.translation.OnePieceSetTranslationMapper;
import com.pcagrade.retriever.card.onepiece.source.official.id.OnePieceOfficialSiteIdMapper;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = PCAMapperConfig.class,
        collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
        uses = {OnePieceSetTranslationMapper.class, OnePieceOfficialSiteIdMapper.class})
public interface OnePieceSetMapper {

    @Mapping(target = "serieId", source = "serie.id")
    OnePieceSetDTO mapToDTO(OnePieceSet set);

    @InheritInverseConfiguration
    @Mapping(target = "officialSiteIds", ignore = true)
    void update(@MappingTarget OnePieceSet set, OnePieceSetDTO dto);

}
