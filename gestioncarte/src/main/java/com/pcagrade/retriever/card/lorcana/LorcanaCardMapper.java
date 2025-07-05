package com.pcagrade.retriever.card.lorcana;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.extraction.status.CardExtractionStatusMapper;
import com.pcagrade.retriever.card.lorcana.transaltion.LorcanaCardTranslationMapper;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = PCAMapperConfig.class,
        collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
        uses = {LorcanaCardTranslationMapper.class, CardExtractionStatusMapper.class})
public interface LorcanaCardMapper {

    @Mapping(target = "artist", source = "artist.name")
    LorcanaCardDTO mapToDTO(LorcanaCard card);

    @AfterMapping
    default void afterMapping(@MappingTarget LorcanaCardDTO dto, LorcanaCard card) {
        dto.setSetIds(card.getCardSets().stream()
                .map(AbstractUlidEntity::getId)
                .toList());
    }

    @Mapping(target = "artist", ignore = true)
    void update(@MappingTarget LorcanaCard card, LorcanaCardDTO dto);
}
