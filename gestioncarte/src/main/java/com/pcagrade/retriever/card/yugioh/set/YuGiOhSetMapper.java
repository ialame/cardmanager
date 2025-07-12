package com.pcagrade.retriever.card.yugioh.set;


import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.yugioh.set.translation.YuGiOhSetTranslationMapper;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = PCAMapperConfig.class,
        collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
        uses = {YuGiOhSetTranslationMapper.class})
public interface YuGiOhSetMapper {

    @Mapping(target = "serieId", source = "serie.id")
    @Mapping(target = "parentId", source = "parent.id")
    YuGiOhSetDTO mapToDTO(YuGiOhSet set);

    @InheritInverseConfiguration
    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "serie", ignore = true)
    @Mapping(target = "parent", ignore = true)
    void update(@MappingTarget YuGiOhSet set, YuGiOhSetDTO dto);
}
