package com.pcagrade.retriever.card.promo.event.translation;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.localization.translation.AbstractTranslationMapper;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Map;

@Mapper(config = PCAMapperConfig.class)
public abstract class PromoCardEventTranslationMapper extends AbstractTranslationMapper<PromoCardEventTranslation, PromoCardEventTranslationDTO> {

    protected PromoCardEventTranslationMapper() {
        super(PromoCardEventTranslation.class);
    }

    public abstract PromoCardEventTranslationDTO mapTranslationToDTO(PromoCardEventTranslation event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "translatable", ignore = true)
    public abstract PromoCardEventTranslation mapDTOToTranslation(PromoCardEventTranslationDTO dto);

    @InheritConfiguration
    public abstract void update(@MappingTarget PromoCardEventTranslation translation, PromoCardEventTranslationDTO dto);

    public Map<Localization, PromoCardEventTranslationDTO> translationMapToDtoMap(Map<Localization, PromoCardEventTranslation> translations) {
        return translationMapToDtoMap(translations, this::mapTranslationToDTO);
    }

    public Map<Localization, PromoCardEventTranslation> dtoMapToTranslationMap(Map<Localization, PromoCardEventTranslationDTO> map) {
        return dtoMapToTranslationMap(map, this::mapDTOToTranslation);
    }

    public Map<Localization, PromoCardEventTranslation> update(@MappingTarget Map<Localization, PromoCardEventTranslation> translations, Map<Localization, PromoCardEventTranslationDTO> map) {
        return this.update(translations, map, this::update, PromoCardEventTranslation::new);
    }

}
