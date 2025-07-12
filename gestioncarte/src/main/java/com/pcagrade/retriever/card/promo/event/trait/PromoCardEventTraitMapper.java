package com.pcagrade.retriever.card.promo.event.trait;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.promo.event.trait.translation.PromoCardEventTraitTranslationMapper;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = PCAMapperConfig.class,
        collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
        uses = {PromoCardEventTraitTranslationMapper.class})
public interface PromoCardEventTraitMapper {

    PromoCardEventTraitDTO mapToDTO(PromoCardEventTrait event);

    void update(@MappingTarget PromoCardEventTrait event, PromoCardEventTraitDTO dto);

    PromoCardEventTrait mapFromDTO(PromoCardEventTraitDTO dto);
}
