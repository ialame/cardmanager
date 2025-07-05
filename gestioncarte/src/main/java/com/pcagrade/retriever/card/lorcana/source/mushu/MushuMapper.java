package com.pcagrade.retriever.card.lorcana.source.mushu;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.lorcana.LorcanaCardDTO;
import com.pcagrade.retriever.card.lorcana.transaltion.LorcanaCardTranslationDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = PCAMapperConfig.class,
        collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE)
public interface MushuMapper {

    LorcanaCardDTO mapToDTO(MushuCard card);

    @AfterMapping
    default void afterMapToDTO(@MappingTarget LorcanaCardDTO dto) {
        dto.getTranslations().forEach((l, t) -> t.setLocalization(l));
    }

    @Mapping(target = "localization", ignore = true)
    @Mapping(target = "fullNumber", source = "number")
    @Mapping(target = "labelName", source = "name")
    LorcanaCardTranslationDTO mapToDTO(MushuCardTranslation translation);

    @AfterMapping
    default void afterMapToDTO(@MappingTarget LorcanaCardTranslationDTO dto) {
        var split = dto.getFullNumber().split("·");

        if (split.length >= 1) {
            dto.setNumber(split[0]);
        } else {
            dto.setNumber("");
        }
    }
}
