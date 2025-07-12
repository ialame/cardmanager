package com.pcagrade.retriever.card.lorcana.transaltion;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.CardTranslation;
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
public abstract class LorcanaCardTranslationMapper extends AbstractTranslationMapper<LorcanaCardTranslation, LorcanaCardTranslationDTO> {

    protected LorcanaCardTranslationMapper() {
        super(LorcanaCardTranslation.class);
    }

    public abstract LorcanaCardTranslationDTO mapTranslationToDTO(LorcanaCardTranslation card);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "translatable", ignore = true)
    @Mapping(target = "labelName", source = "name")
    public abstract LorcanaCardTranslation mapDTOToTranslation(LorcanaCardTranslationDTO dto);

    @InheritConfiguration
    public abstract void update(@MappingTarget LorcanaCardTranslation translation, LorcanaCardTranslationDTO dto);

    public Map<Localization, LorcanaCardTranslationDTO> translationListToDtoMap(List<CardTranslation> translations) {
        return translationListToDtoMap(translations, this::mapTranslationToDTO);
    }

    public List<CardTranslation> dtoMapToTranslationList(Map<Localization, LorcanaCardTranslationDTO> map) {
        return new ArrayList<>(dtoMapToTranslationList(map, this::mapDTOToTranslation));
    }

    public List<CardTranslation> update(@MappingTarget List<CardTranslation> translations, Map<Localization, LorcanaCardTranslationDTO> map) {
        return this.update(translations.stream()
                        .mapMulti(PCAUtils.cast(LorcanaCardTranslation.class))
                        .toList(), map, this::update, LorcanaCardTranslation::new)
                .stream()
                .mapMulti(PCAUtils.cast(CardTranslation.class))
                .toList();
    }
}
