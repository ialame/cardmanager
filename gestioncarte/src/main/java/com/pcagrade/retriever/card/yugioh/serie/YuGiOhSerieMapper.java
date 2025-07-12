package com.pcagrade.retriever.card.yugioh.serie;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.yugioh.serie.translation.YuGiOhSerieTranslationMapper;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = PCAMapperConfig.class,
        collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
        uses = {YuGiOhSerieTranslationMapper.class})
public interface YuGiOhSerieMapper {

    YuGiOhSerieDTO mapToDTO(YuGiOhSerie serie);

    @InheritInverseConfiguration
    void update(@MappingTarget YuGiOhSerie serie, YuGiOhSerieDTO dto);
}
