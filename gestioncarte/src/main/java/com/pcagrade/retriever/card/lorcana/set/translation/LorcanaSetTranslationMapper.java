package com.pcagrade.retriever.card.lorcana.set.translation;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.set.CardSetTranslation;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.localization.translation.AbstractTranslationMapper;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper(config = PCAMapperConfig.class)
public abstract class LorcanaSetTranslationMapper extends AbstractTranslationMapper<CardSetTranslation, LorcanaSetTranslationDTO> {

    protected LorcanaSetTranslationMapper() {
        super(CardSetTranslation.class);
    }

    public abstract LorcanaSetTranslationDTO mapTranslationToDTO(CardSetTranslation set);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "translatable", ignore = true)
    @Mapping(target = "labelName", source = "name")
    public abstract CardSetTranslation mapDTOToTranslation(LorcanaSetTranslationDTO dto);

    @InheritConfiguration
    public abstract void update(@MappingTarget CardSetTranslation translation, LorcanaSetTranslationDTO dto);

    public Map<Localization, LorcanaSetTranslationDTO> translationListToDtoMap(List<CardSetTranslation> translations) {
        return translationListToDtoMap(translations, this::mapTranslationToDTO);
    }

    public List<CardSetTranslation> dtoMapToTranslationList(Map<Localization, LorcanaSetTranslationDTO> map) {
        return new ArrayList<>(dtoMapToTranslationList(map, this::mapDTOToTranslation));
    }

    public List<CardSetTranslation> update(@MappingTarget List<CardSetTranslation> translations, Map<Localization, LorcanaSetTranslationDTO> map) {
        return this.update(translations, map, this::update, CardSetTranslation::new);
    }
}
