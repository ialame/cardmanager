package com.pcagrade.retriever.card.lorcana.set;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.lorcana.set.translation.LorcanaSetTranslationMapper;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = PCAMapperConfig.class,
        collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
        uses = {LorcanaSetTranslationMapper.class})
public interface LorcanaSetMapper {

    @Mapping(target = "serieId", source = "serie.id")
    LorcanaSetDTO mapToDTO(LorcanaSet set);

    @InheritInverseConfiguration
    void update(@MappingTarget LorcanaSet set, LorcanaSetDTO dto);

}
