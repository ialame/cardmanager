package com.pcagrade.retriever.card.promo.version.translation;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.localization.translation.AbstractTranslationMapper;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Map;

@Mapper(config = PCAMapperConfig.class)
public abstract class PromoCardVersionTranslationMapper extends AbstractTranslationMapper<PromoCardVersionTranslation, PromoCardVersionTranslationDTO> {

    protected PromoCardVersionTranslationMapper() {
        super(PromoCardVersionTranslation.class);
    }

    public abstract PromoCardVersionTranslationDTO mapTranslationToDTO(PromoCardVersionTranslation version);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "translatable", ignore = true)
    public abstract PromoCardVersionTranslation mapDTOToTranslation(PromoCardVersionTranslationDTO dto);

    @InheritConfiguration
    public abstract void update(@MappingTarget PromoCardVersionTranslation translation, PromoCardVersionTranslationDTO dto);

    public Map<Localization, PromoCardVersionTranslationDTO> translationMapToDtoMap(Map<Localization, PromoCardVersionTranslation> translations) {
        return translationMapToDtoMap(translations, this::mapTranslationToDTO);
    }

    public Map<Localization, PromoCardVersionTranslation> dtoMapToTranslationMap(Map<Localization, PromoCardVersionTranslationDTO> map) {
        return dtoMapToTranslationMap(map, this::mapDTOToTranslation);
    }

    public Map<Localization, PromoCardVersionTranslation> update(@MappingTarget Map<Localization, PromoCardVersionTranslation> translations, Map<Localization, PromoCardVersionTranslationDTO> map) {
        return this.update(translations, map, this::update, PromoCardVersionTranslation::new);
    }

}
