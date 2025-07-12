package com.pcagrade.retriever.card.tag.translation;

import com.pcagrade.retriever.PCAMapperConfig;
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
public abstract class CardTagTranslationMapper extends AbstractTranslationMapper<CardTagTranslation, CardTagTranslationDTO> {

    protected CardTagTranslationMapper() {
        super(CardTagTranslation.class);
    }

    public abstract CardTagTranslationDTO mapTranslationToDTO(CardTagTranslation tag);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "translatable", ignore = true)
    public abstract CardTagTranslation mapDTOToTranslation(CardTagTranslationDTO dto);

    @InheritConfiguration
    public abstract void update(@MappingTarget CardTagTranslation translation, CardTagTranslationDTO dto);

    public Map<Localization, CardTagTranslationDTO> translationListToDtoMap(List<CardTagTranslation> translations) {
        return translationListToDtoMap(translations, this::mapTranslationToDTO);
    }

    public List<CardTagTranslation> dtoMapToTranslationList(Map<Localization, CardTagTranslationDTO> map) {
        return new ArrayList<>(dtoMapToTranslationList(map, this::mapDTOToTranslation));
    }

    public List<CardTagTranslation> update(@MappingTarget List<CardTagTranslation> translations, Map<Localization, CardTagTranslationDTO> map) {
        return this.update(translations, map, this::update, CardTagTranslation::new);
    }

}
