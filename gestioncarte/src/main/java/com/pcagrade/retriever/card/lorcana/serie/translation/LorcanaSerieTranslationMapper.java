package com.pcagrade.retriever.card.lorcana.serie.translation;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.localization.translation.AbstractTranslationMapper;
import com.pcagrade.retriever.serie.SerieTranslation;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper(config = PCAMapperConfig.class)
public abstract class LorcanaSerieTranslationMapper extends AbstractTranslationMapper<SerieTranslation, LorcanaSerieTranslationDTO> {

    protected LorcanaSerieTranslationMapper() {
        super(SerieTranslation.class);
    }

    public abstract LorcanaSerieTranslationDTO mapTranslationToDTO(SerieTranslation serie);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "translatable", ignore = true)
    public abstract SerieTranslation mapDTOToTranslation(LorcanaSerieTranslationDTO dto);

    @InheritConfiguration
    public abstract void update(@MappingTarget SerieTranslation translation, LorcanaSerieTranslationDTO dto);

    public Map<Localization, LorcanaSerieTranslationDTO> translationListToDtoMap(List<SerieTranslation> translations) {
        return translationListToDtoMap(translations, this::mapTranslationToDTO);
    }

    public List<SerieTranslation> dtoMapToTranslationList(Map<Localization, LorcanaSerieTranslationDTO> map) {
        return new ArrayList<>(dtoMapToTranslationList(map, this::mapDTOToTranslation));
    }

    public List<SerieTranslation> update(@MappingTarget List<SerieTranslation> translations, Map<Localization, LorcanaSerieTranslationDTO> map) {
        return this.update(translations, map, this::update, SerieTranslation::new);
    }
}
