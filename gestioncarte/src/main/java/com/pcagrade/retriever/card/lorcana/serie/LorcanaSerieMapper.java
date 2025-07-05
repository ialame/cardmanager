package com.pcagrade.retriever.card.lorcana.serie;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.lorcana.serie.translation.LorcanaSerieTranslationMapper;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = PCAMapperConfig.class,
        collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
        uses = {LorcanaSerieTranslationMapper.class})
public interface LorcanaSerieMapper {
    LorcanaSerieDTO mapToDTO(LorcanaSerie lorcanaSerie);

    LorcanaSerie maFromDTO(LorcanaSerieDTO lorcanaSerieDTO);

    void update(@MappingTarget LorcanaSerie serie, LorcanaSerieDTO dto);
}
