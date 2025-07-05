package com.pcagrade.retriever.card.promo.version;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.promo.version.translation.PromoCardVersionTranslationMapper;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = PCAMapperConfig.class,
        collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
        uses = {PromoCardVersionTranslationMapper.class})
public interface PromoCardVersionMapper {

    PromoCardVersionDTO mapToDTO(PromoCardVersion version);

    void update(@MappingTarget PromoCardVersion version, PromoCardVersionDTO dto);

}
