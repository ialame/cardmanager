package com.pcagrade.retriever.card.pokemon.bracket.translation;

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
public abstract class BracketTranslationMapper extends AbstractTranslationMapper<BracketTranslation, BracketTranslationDTO> {

    protected BracketTranslationMapper() {
        super(BracketTranslation.class);
    }

    public abstract BracketTranslationDTO mapTranslationToDTO(BracketTranslation bracket);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "translatable", ignore = true)
    public abstract BracketTranslation mapDTOToTranslation(BracketTranslationDTO dto);

    @InheritConfiguration
    public abstract void update(@MappingTarget BracketTranslation translation, BracketTranslationDTO dto);

    public Map<Localization, BracketTranslationDTO> translationListToDtoMap(List<BracketTranslation> translations) {
        return translationListToDtoMap(translations, this::mapTranslationToDTO);
    }

    public List<BracketTranslation> dtoMapToTranslationList(Map<Localization, BracketTranslationDTO> map) {
        return new ArrayList<>(dtoMapToTranslationList(map, this::mapDTOToTranslation));
    }

    public List<BracketTranslation> update(@MappingTarget List<BracketTranslation> translations, Map<Localization, BracketTranslationDTO> map) {
        return this.update(translations, map, this::update, BracketTranslation::new);
    }

}
